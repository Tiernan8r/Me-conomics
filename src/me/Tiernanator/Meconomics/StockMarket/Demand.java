package me.Tiernanator.Meconomics.StockMarket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;

import me.Tiernanator.File.ConfigAccessor;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.SQL.SQLServer;

public class Demand {

	private static MeconomicsMain plugin;
	public static void setPlugin(MeconomicsMain main) {
		plugin = main;
	}

	private int demand;
	private long time;
	private Material material;

	public Demand(Material material, int demand, long time) {
		this.material = material;
		this.demand = demand;
		this.time = time;
	}

	public int getDemand() {
		return this.demand;
	}

	public long getTime() {
		return this.time;
	}

	public Date getDate() {
		return new Date(getTime());
	}

	public Material getMaterial() {
		return this.material;
	}

	public static List<Demand> getAllDemands(Material material) {

		List<Demand> allDemands = new ArrayList<Demand>();

		String query = "SELECT * FROM Demand WHERE Material = '"
				+ material.name() + "';";

		ResultSet resultSet = SQLServer.getResultSet(query);
		try {
			while (resultSet.next()) {

				long time = resultSet.getLong("Date");
				int amountDemanded = resultSet.getInt("Demand");

				Demand demand = new Demand(material, amountDemanded, time);
				allDemands.add(demand);
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allDemands;

	}

	/**
	 * @return The change in demand for the material as a percent.
	 */
	public static double getChangeInDemand(Material material) {
		// Calculate demand from database

		int previousDemand = getPreviousDemand(material);
		int currentDemand = getDailyDemand(material);

		if (previousDemand == 0) {
			return 0;
		}
		int difference = currentDemand - previousDemand;
		double changeInDemand = (difference * 100) / previousDemand;

		return changeInDemand;

	}

	public static double getElasticity(Material material) {

		ConfigAccessor elasticityaccessor = new ConfigAccessor(plugin,
				"elasticities.yml");

		String path = "Elasticity." + material.name();
		double elasticity = elasticityaccessor.getConfig().getDouble(path);

		return elasticity;

	}

	private static int getPreviousDemand(Material material) {

		List<Demand> allDemands = getAllDemands(material);

		if (allDemands == null || allDemands.isEmpty()) {
			return 0;
		}

		Demand recentDemand = allDemands.get(allDemands.size() - 1);
		return recentDemand.getDemand();
	}

	public static void addDemand(Material material, long time, int demand) {

		String statement = "INSERT INTO Demand (Material, Date, Demand) VALUES (?, ?, ?);";
		Object[] values = new Object[]{material.name(), time, demand};

		SQLServer.executePreparedStatement(statement, values);

	}

	public static void addDemand(Demand demand) {
		addDemand(demand.getMaterial(), demand.getTime(), demand.getDemand());
	}

	@SuppressWarnings("unused")
	private static boolean hasPreviousDemand(Material material) {

		List<Demand> allDemands = getAllDemands(material);

		return !(allDemands == null) && !allDemands.isEmpty();

	}

	public static int getDailyDemand(Material material) {

		ConfigAccessor demandAccessor = new ConfigAccessor(plugin,
				"demands.yml");

		int demand = demandAccessor.getConfig()
				.getInt("Demand." + material.name());
		return demand;

	}

	public static void setDailyDemand(Material material, int demand) {

		ConfigAccessor demandAccessor = new ConfigAccessor(plugin,
				"demands.yml");

		demandAccessor.getConfig().set("Demand." + material.name(), demand);

		demandAccessor.saveConfig();

	}

	public static void incrementDailyDemand(Material material, int amount) {

		int demand = getDailyDemand(material);
		demand += amount;
		setDailyDemand(material, demand);

	}

	public static void removeDemand(Material material, long date) {

		String statement = "DELETE FROM Demand WHERE Material = ? AND Date = ?;";
		Object[] values = new Object[] {material.name(), date};
		SQLServer.executePreparedStatement(statement, values);

	}

	public static void removeDemand(Demand demand) {
		removeDemand(demand.getMaterial(), demand.getTime());
	}

}

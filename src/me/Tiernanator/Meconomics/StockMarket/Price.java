package me.Tiernanator.Meconomics.StockMarket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;

import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Utilities.File.ConfigAccessor;
import me.Tiernanator.Utilities.SQL.SQLServer;

public class Price {

	private static MeconomicsMain plugin;
	public static void setPlugin(MeconomicsMain main) {
		plugin = main;
	}

	private double price;
	private long time;
	private Material material;

	public Price(Material material, double price, long time) {
		this.material = material;
		this.price = Currency.roundCurrency(price);
		this.time = time;
	}

	public double getPrice() {
		return this.price;
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

	public static List<Price> getAllPrices(Material material) {

		List<Price> allPrices = new ArrayList<Price>();

		String query = "SELECT * FROM ItemPrices WHERE Material = '"
				+ material.name() + "';";
		ResultSet resultSet = SQLServer.getResultSet(query);

		try {
			if (!resultSet.isBeforeFirst()) {
				return allPrices;
			}
			while (resultSet.next()) {

				long time = resultSet.getLong("Date");
				double amountPrice = resultSet.getDouble("Price");

				Price price = new Price(material, amountPrice, time);
				allPrices.add(price);
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allPrices;
	}

	public static double getPrice(Material material) {

		double percentChangeDemand = Demand.getChangeInDemand(material);
		double elasticity = Demand.getElasticity(material);

		double percentChangePrice = percentChangeDemand / elasticity;

		double previousPrice = getPreviousPrice(material);
		double price = previousPrice
				+ previousPrice * (percentChangePrice / 100);

		price = Currency.roundCurrency(price);

		if (price < 0) {
			price = 0.01;
		}

		return price;

	}

	private static double getDefaultPrice(Material material) {

		ConfigAccessor priceAccessor = new ConfigAccessor(plugin,
				"defaultPrices.yml");
		double price = priceAccessor.getConfig()
				.getDouble("Price." + material.name());
		return price;

	}

	private static double getPreviousPrice(Material material) {

		List<Price> allPrices = getAllPrices(material);

		if (allPrices == null || allPrices.isEmpty()) {
			return getDefaultPrice(material);
		}

		Price recentPrice = allPrices.get(allPrices.size() - 1);
		return recentPrice.getPrice();
	}

	public static void addPrice(Material material, long time, double price) {

		double roundedPrice = Currency.roundCurrency(price);

		List<Price> allPrices = getAllPrices(material);
		if (allPrices != null) {
			if (allPrices.size() > 27) {
				Price oldestPrice = allPrices.get(0);
				removePrice(oldestPrice);
			}
		}

		String statement = "INSERT INTO ItemPrices (Material, Date, Price) VALUES (?, ?, ?);";
		Object[] values = new Object[]{material.name(), time, roundedPrice};
		SQLServer.executePreparedStatement(statement, values);

	}

	public static void addPrice(Price price) {
		addPrice(price.getMaterial(), price.getTime(), price.getPrice());
	}

	private static boolean hasPreviousPrice(Material material) {

		List<Price> allPrices = getAllPrices(material);

		return !(allPrices == null) && !allPrices.isEmpty();

	}

	public static void removePrice(Material material, long date) {

		if (!hasPreviousPrice(material)) {
			return;
		}

		String statement = "DELETE FROM ItemPrices WHERE Material = ? AND Date = ?;";
		Object[] values = new Object[]{material.name(), date};
		SQLServer.executePreparedStatement(statement, values);

	}

	public static void removePrice(Price price) {
		removePrice(price.getMaterial(), price.getTime());
	}

}

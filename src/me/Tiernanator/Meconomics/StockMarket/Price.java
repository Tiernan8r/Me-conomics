package me.Tiernanator.Meconomics.StockMarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import me.Tiernanator.File.ConfigAccessor;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.Main;

public class Price {

	private static Main plugin;
	public static void setPlugin(Main main) {
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

		String query = "SELECT * FROM ItemPrices WHERE Material = ?;";

		Connection connection = Main.getSQL().getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, material.name());
			ResultSet resultSet = null;
			resultSet = preparedStatement.executeQuery();
			if (!resultSet.isBeforeFirst()) {
				return allPrices;
			}
			while (resultSet.next()) {

				long time = resultSet.getLong("Date");
				double amountPrice = resultSet.getDouble("Price");

				Price price = new Price(material, amountPrice, time);
				allPrices.add(price);
			}
			preparedStatement.closeOnCompletion();
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

		final double roundedPrice = Currency.roundCurrency(price);
		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {

				List<Price> allPrices = getAllPrices(material);
				if (allPrices != null) {
					if (allPrices.size() > 27) {
						Price oldestPrice = allPrices.get(0);
						removePrice(oldestPrice);
					}
				}

				// double price = Currency.roundCurrency(price);

				String query = "INSERT INTO ItemPrices (Material, Date, Price) VALUES ('"
						+ material.name() + "', '" + time + "', '"
						+ roundedPrice + "');";

				Connection connection = Main.getSQL().getConnection();
				Statement statement = null;
				try {
					statement = connection.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					statement.execute(query);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
		runnable.runTaskAsynchronously(plugin);

		// List<Price> allPrices = getAllPrices(material);
		// if(allPrices != null) {
		// if(allPrices.size() > 27) {
		// Price oldestPrice = allPrices.get(0);
		// removePrice(oldestPrice);
		// }
		// }
		//
		// price = Currency.roundCurrency(price);
		//
		// String query = "INSERT INTO ItemPrices (Material, Date, Price) VALUES
		// ('" + material.name() + "', '" + time + "', '" + price + "');";
		//
		// Connection connection = Main.getSQL().getConnection();
		// Statement statement = null;
		// try {
		// statement = connection.createStatement();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// try {
		// statement.execute(query);
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

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

		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {

				String query = "DELETE FROM ItemPrices " + "WHERE Material = '"
						+ material.name() + "' AND " + "Date = '" + date + "';";

				Connection connection = Main.getSQL().getConnection();
				Statement statement = null;
				try {
					statement = connection.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					statement.execute(query);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
		runnable.runTaskAsynchronously(plugin);

		// String query = "DELETE FROM ItemPrices "
		// + "WHERE Material = '" + material.name() + "' AND "
		// + "Date = '" + date + "';";
		//
		// Connection connection = Main.getSQL().getConnection();
		// Statement statement = null;
		// try {
		// statement = connection.createStatement();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// try {
		// statement.execute(query);
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	public static void removePrice(Price price) {
		removePrice(price.getMaterial(), price.getTime());
	}

}

package me.Tiernanator.Meconomics;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Currency implements Listener {

	public Currency(Main main) {
	}

	public static double getPlayerBalance(Player player) {

		String playerUUID = player.getUniqueId().toString();
		return getPlayerBalance(playerUUID);

	}
	
	public static double getPlayerBalance(String playerUUID) {

		String query = "SELECT Money FROM Balance WHERE UUID = '" + playerUUID
				+ "';";

		Connection connection = Main.getSQL().getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (!resultSet.isBeforeFirst()) {
				return 0.0;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			resultSet.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		double balance = 0.0;
		try {
			balance = resultSet.getDouble("Money");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return balance;

	}

	public static void setPlayerBalance(Player player, double amount) {

		String playerUUID = player.getUniqueId().toString();

		setPlayerBalance(playerUUID, amount);
	}

	public static void setPlayerBalance(String playerUUID, double amount) {

		amount = roundCurrency(amount);

		String query = "UPDATE Balance SET Money = '" + amount
				+ "' WHERE UUID = '" + playerUUID + "';";

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

	public static void addToPlayerBalance(Player player, double amount) {

		String playerUUID = player.getUniqueId().toString();
		addToPlayerBalance(playerUUID, amount);
	}
	
	public static void addToPlayerBalance(String playerUUID, double amount) {

		double currentBalance = getPlayerBalance(playerUUID);
		double newBalance = currentBalance + amount;
		setPlayerBalance(playerUUID, newBalance);

	}

	public static double roundCurrency(double money) {

		return Math.round(money * 100) / 100.0;

	}

}

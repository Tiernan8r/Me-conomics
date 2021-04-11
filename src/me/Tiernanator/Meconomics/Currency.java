package me.Tiernanator.Meconomics;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.Tiernanator.Utilities.SQL.SQLServer;

public class Currency implements Listener {

	public Currency(MeconomicsMain main) {
	}

	public static double getPlayerBalance(Player player) {

		String playerUUID = player.getUniqueId().toString();
		return getPlayerBalance(playerUUID);

	}
	
	public static double getPlayerBalance(String playerUUID) {

		String query = "SELECT Money FROM Balance WHERE UUID = '" + playerUUID
				+ "';";

		return SQLServer.getFloat(query, "Money");

	}

	public static void setPlayerBalance(Player player, double amount) {

		String playerUUID = player.getUniqueId().toString();

		setPlayerBalance(playerUUID, amount);
	}

	public static void setPlayerBalance(String playerUUID, double amount) {

		amount = roundCurrency(amount);

		String statement = "UPDATE Balance SET Money = ? WHERE UUID = ?;";
		Object[] values = new Object[] {amount, playerUUID};
		SQLServer.executePreparedStatement(statement, values);
		
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

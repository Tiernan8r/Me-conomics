package me.Tiernanator.Meconomics.StockMarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.Tiernanator.File.Log;
import me.Tiernanator.Meconomics.Main;
import me.Tiernanator.Utilities.Blocks.MultiBlocks;
import me.Tiernanator.Utilities.Items.Item;
import me.Tiernanator.Utilities.Players.GetPlayer;
import me.Tiernanator.Utilities.Players.PlayerLogger;

public class ShopBlock {

	private static Main plugin;

	public static void setPlugin(Main main) {
		plugin = main;
	}

	public static ItemStack resolveLorePricing(ItemStack item) {

		Log log = Main.getLog();
		
//		Logger logger = plugin.getLogger();
		log.log("Beginning sales data lore cleanup for item: "
						+ item.getType().name() + ":" + item.getDurability()
						+ " x " + item.getAmount() + ":");

		List<String> lore = new ArrayList<String>();
		if (!hasLorePricing(item)) {
			log.log(Level.FINE, "- Item has no lore pricing, finishing.");
			log.log(Level.FINE, "Done.");
			return item;
		}

		lore = item.getItemMeta().getLore();
		log.log("- Getting item lore");

		for (int i = 0; i < 2; i++) {
			log.log("- Round " + (i + 1) + " of lore cleanup:");
			for (int j = 0; j < lore.size(); j++) {
				String itemLore = lore.get(j);
				log.log(" - The item lore is: " + itemLore);

				if (itemLore == null) {
					continue;
				}
				if (itemLore.contains(ChatColor.DARK_PURPLE + "- ")
						&& (itemLore.contains("sold today")
								|| itemLore.contains("£"))) {
					lore.remove(itemLore);
					log.log(" - Lore was sale info related, removing.");
				}

			}
			Item.setLore(item, lore);
			log.log("- Round " + (i + 1) + " of lore cleanup done.");
		}
		log.log("Done.");
		return item;
	}

	public static boolean hasLorePricing(ItemStack item) {

		List<String> lore = new ArrayList<String>();
		if (!Item.hasLore(item)) {
			return false;
		}

		lore = item.getItemMeta().getLore();

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < lore.size(); j++) {
				String itemLore = lore.get(j);

				if (itemLore == null) {
					continue;
				}
				if (itemLore.contains(ChatColor.DARK_PURPLE + "- ")
						&& (itemLore.contains("sold today")
								|| itemLore.contains("£"))) {
					return true;
				}
			}
		}
		return false;
	}

	public static Block getBlock(Player player) {

		String playerUUID = player.getUniqueId().toString();

		return getBlock(playerUUID);
	}

	public static Block getBlock(String playerUUID) {

		String query = "SELECT * FROM ShopBlocks WHERE " + "Owner = '"
				+ playerUUID + "';";

		Connection connection = Main.getSQL().getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		Statement statement = null;
//		try {
//			statement = connection.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		ResultSet resultSet = null;
		try {
//			resultSet = statement.executeQuery(query);
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (!resultSet.isBeforeFirst()) {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int x = 0;
		int y = 0;
		int z = 0;
		String worldName = null;
		try {
			x = resultSet.getInt("X");
			y = resultSet.getInt("Y");
			z = resultSet.getInt("Z");
			worldName = resultSet.getString("World");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (worldName == null) {
			return null;
		}

		World world = plugin.getServer().getWorld(worldName);
		Location location = new Location(world, x, y, z);

		Block block = location.getBlock();
		block = MultiBlocks.getCorrectBlock(block);

		try {
			preparedStatement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return block;
	}

	public static int getBlockIndex(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		if (!isShop(block)) {
			return -1;
		}
		Location location = block.getLocation();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world = location.getWorld().getName();

		String query = "SELECT ID FROM ShopBlocks WHERE " + "World = '" + world
				+ "' AND " + "X = '" + x + "' AND " + "Y = '" + y + "' AND "
				+ "Z = '" + z + "';";

		Connection connection = Main.getSQL().getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (!resultSet.isBeforeFirst()) {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int index = -1;
		try {
			index = resultSet.getInt("ID");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return index;
	}

	public static boolean isShop(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		Location blockLocation = block.getLocation();
		int x = blockLocation.getBlockX();
		int y = blockLocation.getBlockY();
		int z = blockLocation.getBlockZ();
		String world = blockLocation.getWorld().getName();

		String query = "SELECT * FROM ShopBlocks WHERE " + "World = '" + world
				+ "' AND " + "X = '" + x + "' AND " + "Y = '" + y + "' AND "
				+ "Z = '" + z + "';";

		Connection connection = Main.getSQL().getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (!resultSet.isBeforeFirst()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		World thisWorld = null;
		int thisX = 0;
		int thisY = 0;
		int thisZ = 0;

		Location thisBlockLocation = null;

		try {
			String worldName = resultSet.getString("World");
			thisWorld = plugin.getServer().getWorld(worldName);

			thisX = resultSet.getInt("X");
			thisY = resultSet.getInt("Y");
			thisZ = resultSet.getInt("Z");

			thisBlockLocation = new Location(thisWorld, thisX, thisY, thisZ);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return thisBlockLocation.equals(blockLocation);
	}

	public static boolean isShop(String worldName, int x, int y, int z) {

		World world = plugin.getServer().getWorld(worldName);
		Block block = new Location(world, x, y, z).getBlock();
		return isShop(block);

	}

	public static void addShopBlock(int x, int y, int z, String world,
			Player player) {

		if (isShop(world, x, y, z) || hasShop(player)) {
			return;
		}

		String playerUUID = player.getUniqueId().toString();

		BukkitRunnable runnable = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				Connection connection = Main.getSQL().getConnection();
				PreparedStatement preparedStatement = null;
				try {
					preparedStatement = connection.prepareStatement(
							"INSERT INTO ShopBlocks (World, X, Y, Z, Owner) VALUES (?, ?, ?, ?, ?);");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setString(1, world);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setInt(2, x);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setInt(3, y);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setInt(4, z);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.setString(5, playerUUID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		};
		runnable.runTaskAsynchronously(plugin);
		
//		Connection connection = Main.getSQL().getConnection();
//		PreparedStatement preparedStatement = null;
//		try {
//			preparedStatement = connection.prepareStatement(
//					"INSERT INTO ShopBlocks (World, X, Y, Z, Owner) VALUES (?, ?, ?, ?, ?);");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.setString(1, world);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.setInt(2, x);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.setInt(3, y);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.setInt(4, z);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.setString(5, playerUUID);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			preparedStatement.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

	}

	public static void addShopBlock(Block block, Player player) {

		block = MultiBlocks.getCorrectBlock(block);

		Location location = block.getLocation();
//		return addShopBlock(location, player);
		addShopBlock(location, player);
	}

	public static void addShopBlock(Location location, Player player) {

		location = MultiBlocks.getCorrectBlock(location.getBlock())
				.getLocation();

//		return addShopBlock(location.getBlockX(), location.getBlockY(),
//		location.getBlockZ(), location.getWorld().getName(), player);
		addShopBlock(location.getBlockX(), location.getBlockY(),
				location.getBlockZ(), location.getWorld().getName(), player);
	}

	public static boolean hasShop(Player player) {

		String playerUUID = player.getUniqueId().toString();
		return hasShop(playerUUID);

	}

	public static boolean hasShop(String playerUUID) {

		Block block = getBlock(playerUUID);
		return !(block == null);

	}

	public static Player getShopOwner(Block block) {

//		block = MultiBlocks.getCorrectBlock(block);
//
//		Location blockLocation = block.getLocation();
//		int x = blockLocation.getBlockX();
//		int y = blockLocation.getBlockY();
//		int z = blockLocation.getBlockZ();
//		String world = blockLocation.getWorld().getName();
//
//		String query = "SELECT Owner FROM ShopBlocks WHERE " + "World = '"
//				+ world + "' AND " + "X = '" + x + "' AND " + "Y = '" + y
//				+ "' AND " + "Z = '" + z + "';";
//
//		Connection connection = Main.getSQL().getConnection();
//		PreparedStatement preparedStatement = null;
//		try {
//			preparedStatement = connection.prepareStatement(query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		ResultSet resultSet = null;
//		try {
//			resultSet = preparedStatement.executeQuery();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			if (!resultSet.isBeforeFirst()) {
//				return null;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			resultSet.next();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		String playerUUID = "";
//		try {
//			playerUUID = resultSet.getString("Owner");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		String ownerUUID = getOwnerUUID(block);
		PlayerLogger playerLogger = new PlayerLogger();
		String playerName = playerLogger.getPlayerNameByUUID(ownerUUID);

		Player player = GetPlayer.functionGetPlayer(playerName);
		return player;
	}

	public static String getOwnerUUID(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		Location blockLocation = block.getLocation();
		int x = blockLocation.getBlockX();
		int y = blockLocation.getBlockY();
		int z = blockLocation.getBlockZ();
		String world = blockLocation.getWorld().getName();

		String query = "SELECT Owner FROM ShopBlocks WHERE " + "World = '"
				+ world + "' AND " + "X = '" + x + "' AND " + "Y = '" + y
				+ "' AND " + "Z = '" + z + "';";

		Connection connection = Main.getSQL().getConnection();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (!resultSet.isBeforeFirst()) {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String playerUUID = "";
		try {
			playerUUID = resultSet.getString("Owner");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerUUID;
	}

	public static void setOwner(Block block, String playerUUID) {

		BukkitRunnable runnable = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				Block correctBlock = MultiBlocks.getCorrectBlock(block);

				Location blockLocation = correctBlock.getLocation();
				int x = blockLocation.getBlockX();
				int y = blockLocation.getBlockY();
				int z = blockLocation.getBlockZ();
				String world = blockLocation.getWorld().getName();

				String query = "UPDATE ShopBlocks " + "SET Owner = '" + playerUUID + "'"
						+ " WHERE " + "World = '" + world + "' AND " + "X = '" + x
						+ "' AND " + "Y = '" + y + "' AND " + "Z = '" + z + "';";

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
		
//		block = MultiBlocks.getCorrectBlock(block);
//
//		Location blockLocation = block.getLocation();
//		int x = blockLocation.getBlockX();
//		int y = blockLocation.getBlockY();
//		int z = blockLocation.getBlockZ();
//		String world = blockLocation.getWorld().getName();
//
//		String query = "UPDATE ShopBlocks " + "SET Owner = '" + playerUUID + "'"
//				+ " WHERE " + "World = '" + world + "' AND " + "X = '" + x
//				+ "' AND " + "Y = '" + y + "' AND " + "Z = '" + z + "';";
//
//		Connection connection = Main.getSQL().getConnection();
//		Statement statement = null;
//		try {
//			statement = connection.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			statement.execute(query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return true;
	}

	public static void removeShopBlock(int x, int y, int z, String world) {
		
		BukkitRunnable runnable = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				String query = "DELETE FROM ShopBlocks " + "WHERE WORLD = '" + world
						+ "' AND " + "X = '" + x + "' AND " + "Y = '" + y + "' AND "
						+ "Z = '" + z + "';";

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
		
//		String query = "DELETE FROM ShopBlocks " + "WHERE WORLD = '" + world
//				+ "' AND " + "X = '" + x + "' AND " + "Y = '" + y + "' AND "
//				+ "Z = '" + z + "';";
//
//		Connection connection = Main.getSQL().getConnection();
//		Statement statement = null;
//		try {
//			statement = connection.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		try {
//			statement.execute(query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

	public static void removeShopBlock(Location location) {

		location = MultiBlocks.getCorrectBlock(location.getBlock())
				.getLocation();

		removeShopBlock(location.getBlockX(), location.getBlockY(),
				location.getBlockZ(), location.getWorld().getName());

	}

	public static void removeShopBlock(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		Location location = block.getLocation();
		removeShopBlock(location);

	}

}

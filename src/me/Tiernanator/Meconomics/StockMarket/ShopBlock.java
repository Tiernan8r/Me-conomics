package me.Tiernanator.Meconomics.StockMarket;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Utilities.Blocks.MultiBlocks;
import me.Tiernanator.Utilities.File.Log;
import me.Tiernanator.Utilities.Items.ItemUtility;
import me.Tiernanator.Utilities.Players.GetPlayer;
import me.Tiernanator.Utilities.SQL.SQLServer;

public class ShopBlock {

	private static MeconomicsMain plugin;

	public static void setPlugin(MeconomicsMain main) {
		plugin = main;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack resolveLorePricing(ItemStack item) {

		Log log = MeconomicsMain.getLog();

		log.log("Beginning sales data lore cleanup for item: "
				+ item.getType().name() + ":" + item.getDurability() + " x "
				+ item.getAmount() + ":");

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
								|| itemLore.contains("�"))) {
					lore.remove(itemLore);
					log.log(" - Lore was sale info related, removing.");
				}

			}
			ItemUtility.setLore(item, lore);
			log.log("- Round " + (i + 1) + " of lore cleanup done.");
		}
		log.log("Done.");
		return item;
	}

	public static boolean hasLorePricing(ItemStack item) {

		List<String> lore = new ArrayList<String>();
		if (!ItemUtility.hasLore(item)) {
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
								|| itemLore.contains("�"))) {
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

		String query = "SELECT * FROM ShopBlocks WHERE Owner = '" + playerUUID
				+ "';";

		Location location = SQLServer.getLocation(query);

		Block block = location.getBlock();
		block = MultiBlocks.getCorrectBlock(block);

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

		String query = "SELECT ID FROM ShopBlocks WHERE World = '" + world
				+ "' AND X = '" + x + "' AND Y = '" + y + "' AND Z = '" + z
				+ "';";

		return SQLServer.getInt(query, "ID");

	}

	public static boolean isShop(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		Location blockLocation = block.getLocation();
		int x = blockLocation.getBlockX();
		int y = blockLocation.getBlockY();
		int z = blockLocation.getBlockZ();
		String world = blockLocation.getWorld().getName();

		String query = "SELECT * FROM ShopBlocks WHERE World = '" + world
				+ "' AND X = '" + x + "' AND Y = '" + y + "' AND Z = '" + z
				+ "';";

		Location storedLocation = SQLServer.getLocation(query);

		if (storedLocation == null) {
			return false;
		}

		return storedLocation.equals(blockLocation);
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

		String statement = "INSERT INTO ShopBlocks (World, X, Y, Z, Owner) VALUES (?, ?, ?, ?, ?);";
		Object[] values = new Object[]{world, x, y, z, playerUUID};
		SQLServer.executePreparedStatement(statement, values);
	}

	public static void addShopBlock(Block block, Player player) {

		block = MultiBlocks.getCorrectBlock(block);

		Location location = block.getLocation();
		addShopBlock(location, player);

	}

	public static void addShopBlock(Location location, Player player) {

		location = MultiBlocks.getCorrectBlock(location.getBlock())
				.getLocation();

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

		String ownerUUID = getOwnerUUID(block);

		Player player = GetPlayer.getPlayerByUUID(ownerUUID);
		return player;

	}

	public static String getOwnerUUID(Block block) {

		block = MultiBlocks.getCorrectBlock(block);

		Location blockLocation = block.getLocation();
		int x = blockLocation.getBlockX();
		int y = blockLocation.getBlockY();
		int z = blockLocation.getBlockZ();
		String world = blockLocation.getWorld().getName();

		String query = "SELECT Owner FROM ShopBlocks WHERE World = '" + world
				+ "' AND X = '" + x + "' AND Y = '" + y + "' AND Z = '" + z
				+ "';";

		return SQLServer.getString(query, "Owner");
	}

	public static void setOwner(Block block, String playerUUID) {

		Block correctBlock = MultiBlocks.getCorrectBlock(block);

		Location blockLocation = correctBlock.getLocation();
		int x = blockLocation.getBlockX();
		int y = blockLocation.getBlockY();
		int z = blockLocation.getBlockZ();
		String world = blockLocation.getWorld().getName();

		String statement = "UPDATE ShopBlocks SET Owner = ? WHERE World = ? AND X = ? AND Y = ? AND Z = ?;";
		Object[] values = new Object[]{playerUUID, world, x, y, z};
		SQLServer.executePreparedStatement(statement, values);

	}

	public static void removeShopBlock(int x, int y, int z, String world) {

		String statement = "DELETE FROM ShopBlocks WHERE WORLD = ? AND X = ? AND Y = ? AND Z = ?;";
		Object[] values = new Object[]{world, x, y, z};
		SQLServer.executePreparedStatement(statement, values);

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

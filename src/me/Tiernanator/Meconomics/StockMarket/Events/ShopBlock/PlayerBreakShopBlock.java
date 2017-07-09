package me.Tiernanator.Meconomics.StockMarket.Events.ShopBlock;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Utilities.Blocks.MultiBlocks;
import me.Tiernanator.Utilities.Players.PlayerLogger;

public class PlayerBreakShopBlock implements Listener {

	private static MeconomicsMain plugin;
	private ChatColor warning = Colour.WARNING.getColour();
	private ChatColor highlight = Colour.HIGHLIGHT.getColour();
	private ChatColor informative = Colour.INFORMATIVE.getColour();

	public PlayerBreakShopBlock(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void breakShopBlock(BlockBreakEvent event) {

		if(event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		block = MultiBlocks.getCorrectBlock(block);
		
		if(!(block.getState() instanceof Chest)) {
			return;
		}

		boolean isShop = ShopBlock.isShop(block);
		if (!isShop) {
			return;
		}

		Player player = event.getPlayer();

		String playerUUID = player.getUniqueId().toString();
		
		String ownerUUID = ShopBlock.getOwnerUUID(block);
		
		if(!ownerUUID.equalsIgnoreCase(playerUUID)) {
			event.setCancelled(true);
			PlayerLogger playerLogger = new PlayerLogger();
			String ownerName = playerLogger.getPlayerNameByUUID(ownerUUID);
			player.sendMessage(warning + "This chest is " + highlight + ownerName + warning + "'s shop and you cannot destroy it.");
			return;
		}
		
		event.setCancelled(true);
		String removeShopCode = plugin.getConfig().getString("Remove Shop Code");
		
		player.sendMessage(warning
				+ "This chest is your shop and you cannot break it, to remove your shop enter the code: " + informative + removeShopCode + warning + " on a sign on this chest, and shift click on it to de-register it as your shop.");
		
	}

}

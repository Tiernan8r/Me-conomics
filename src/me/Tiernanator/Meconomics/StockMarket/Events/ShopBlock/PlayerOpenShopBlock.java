package me.Tiernanator.Meconomics.StockMarket.Events.ShopBlock;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Meconomics.Main;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Utilities.Blocks.MultiBlocks;
import me.Tiernanator.Utilities.Players.PlayerLogger;

public class PlayerOpenShopBlock implements Listener {

	@SuppressWarnings("unused")
	private static Main plugin;
	
	private ChatColor warning = Colour.WARNING.getColour();
	private ChatColor highlight = Colour.HIGHLIGHT.getColour();

	public PlayerOpenShopBlock(Main main) {
		plugin = main;
	}

	@EventHandler
	public void shopBlockOpen(InventoryOpenEvent event) {

		if(event.isCancelled()) {
			return;
		}
		
		//If the inventory is one opened by the MenuAPI it doesn't have an associated block...
		Block block = null;
		try {
			block = event.getInventory().getLocation().getBlock();
		} catch (Exception e) {
			return;
		}
		if(block == null) {
			return;
		}
		
		block = MultiBlocks.getCorrectBlock(block);

		if(!(block.getState() instanceof Chest)) {
			return;
		}

		boolean isShop = ShopBlock.isShop(block);
		if (!isShop) {
			return;
		}

		Player player = (Player) event.getPlayer();

		String playerUUID = player.getUniqueId().toString();
		
		String ownerUUID = ShopBlock.getOwnerUUID(block);
		
		if(!ownerUUID.equalsIgnoreCase(playerUUID)) {
			event.setCancelled(true);
			PlayerLogger playerLogger = new PlayerLogger();
			String ownerName = playerLogger.getPlayerNameByUUID(ownerUUID);
			player.sendMessage(warning + "This chest is " + highlight + ownerName + warning + "'s shop and you cannot open it.");
			return;
		}
		
		Inventory inventory = event.getInventory();
		for(ItemStack item : inventory.getContents()) {
			if(item == null) {
				continue;
			}
			ShopBlock.resolveLorePricing(item);
		}
		
	}

}

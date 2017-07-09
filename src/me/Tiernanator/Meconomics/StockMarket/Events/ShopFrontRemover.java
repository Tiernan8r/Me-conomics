package me.Tiernanator.Meconomics.StockMarket.Events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.MagicSigns.SignBlock;
import me.Tiernanator.MagicSigns.Events.CustomEvents.CustomSignClickEvent;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Utilities.Players.SelectAction;

public class ShopFrontRemover implements Listener {

	private static MeconomicsMain plugin;

	private ChatColor bad = Colour.BAD.getColour();
	private ChatColor warning = Colour.WARNING.getColour();
	private ChatColor good = Colour.GOOD.getColour();

	public ShopFrontRemover(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void deRegisterShopFront(CustomSignClickEvent event) {

		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Sign sign = event.getSign();
		
		SelectAction selectAction = event.getSelectAction();
		if(selectAction != SelectAction.RIGHT_CLICK) {
			return;
		}

		if(!player.isSneaking()) {
			return;
		}
		
		String shopCode = plugin.getConfig().getString("Remove Shop Code");
		String[] lines = event.getSignText();
		boolean hasShopCode = false;
		for (int i = 0; i < lines.length; i++) {
			String text = lines[i];
			if (text.contains(shopCode)) {
				text = text.replace(shopCode, "");
				sign.setLine(i, text);
				sign.update(true);
				hasShopCode = true;
			}
		}
		if (!hasShopCode) {
			event.setCancelled(true);
			return;
		}
		
		Block block = SignBlock.getAttachedToBlock(sign);
		if(block == null) {
			player.sendMessage(warning + "Your sign must be attached to a chest to manipulate your shop front.");
			event.setCancelled(true);
			return;
		}
		
		Material blockMaterial = block.getType();

		if (blockMaterial != Material.CHEST
				&& blockMaterial != Material.TRAPPED_CHEST) {
			player.sendMessage(bad
					+ "To manipulate a Shopfront, this sign must be on a chest.");
			event.setCancelled(true);
			return;
		}

		
		if(ShopBlock.getBlock(player) == null) {
			
			event.setCancelled(true);
			player.sendMessage(bad + "You don't have a shop.");
			return;
			
		}
		
		ShopBlock.removeShopBlock(block);
		player.sendMessage(good + "Your shop has been removed from the market.");
		
//		if(!(block instanceof CraftChest)) {
//			return;
//		}
//		
//		CraftChest chest = (CraftChest) block;
//		Inventory inventory = chest.getBlockInventory();
//		for(int i = 0; i < inventory.getSize(); i++) {
//			inventory.setItem(i, Item.renameItem(Material.EMERALD, "SHOP"));
//		}
		
		
		
	}

}

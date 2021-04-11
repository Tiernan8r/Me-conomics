package me.Tiernanator.Meconomics.StockMarket.Events;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Tiernanator.MagicSigns.SignBlock;
import me.Tiernanator.MagicSigns.Events.CustomEvents.CustomSignClickEvent;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Utilities.Colours.Colour;
import me.Tiernanator.Utilities.Players.PlayerLogger;
import me.Tiernanator.Utilities.Players.SelectAction;

public class ShopFrontInitialiser implements Listener {

	private static MeconomicsMain plugin;

	private ChatColor bad = Colour.BAD.getColour();
	private ChatColor warning = Colour.WARNING.getColour();
	private ChatColor good = Colour.GOOD.getColour();
	private ChatColor highlight = Colour.HIGHLIGHT.getColour();

	public ShopFrontInitialiser(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void registerShopFront(CustomSignClickEvent event) {

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
		
		String shopCode = plugin.getConfig().getString("Create Shop Code");
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
			return;
		}
		
		Block block = SignBlock.getAttachedToBlock(sign);
		if(block == null) {
			player.sendMessage(warning + "Your sign must be attached to a chest to create a shop front.");
			event.setCancelled(true);
			return;
		}
		
//		Material blockMaterial = block.getType();
//
//		if (blockMaterial != Material.CHEST
//				&& blockMaterial != Material.TRAPPED_CHEST) {
//			player.sendMessage(bad
//					+ "To create a Shopfront, this sign must be on a chest.");
//			event.setCancelled(true);
//			return;
//		}
		if(!(block.getState() instanceof Chest)) {
			player.sendMessage(bad
					+ "To create a Shopfront, this sign must be on a chest.");
			event.setCancelled(true);
			return;
		}
		
		boolean hasShop = ShopBlock.hasShop(player);
		if(hasShop) {
			player.sendMessage(bad + "You can't add a shop if you already have one!");
			event.setCancelled(true);
			return;
		}
		
		boolean isShop = ShopBlock.isShop(block);
		if(isShop) {
			
			String ownerUUID = ShopBlock.getOwnerUUID(block);
			String ownerName = PlayerLogger.getPlayerNameByUUID(ownerUUID);
			player.sendMessage(bad + "This chest has already been registered as a shop by " + highlight + ownerName + bad + ".");
			event.setCancelled(true);
			return;
		}
		
		ShopBlock.addShopBlock(block, player);
		player.sendMessage(good + "Your shop has been added to the open market.");
		
	}

}

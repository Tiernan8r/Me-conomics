package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.MagicSigns.Events.CustomEvents.CustomSignClickEvent;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Menu.Menu;
import me.Tiernanator.Menu.MenuAction;
import me.Tiernanator.Menu.MenuEntry;
import me.Tiernanator.Utilities.Players.SelectAction;

public class Shopper implements Listener {

	private static MeconomicsMain plugin;

	private ChatColor bad = Colour.BAD.getColour();

	public Shopper(MeconomicsMain main) {
		plugin = main;
	}

	/**
	 * Handles player browsing through other players' shops
	 * @param event
	 */
	@EventHandler
	public void openMarketPlaceOnSignClick(CustomSignClickEvent event) {

		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		SelectAction selectAction = event.getSelectAction();
		if(selectAction != SelectAction.RIGHT_CLICK) {
			return;
		}

		String shopCode = plugin.getConfig().getString("Shopper Code");
		String[] lines = event.getSignText();
		boolean hasShopCode = false;
		for (int i = 0; i < lines.length; i++) {
			String text = lines[i];
			if (text.contains(shopCode)) {
				hasShopCode = true;
			}
		}
		if (!hasShopCode) {
			return;
		}
		
		List<MenuEntry> menuEntries = new ArrayList<MenuEntry>();
		
		OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();
		
		String playerUUID = player.getUniqueId().toString();
		for(OfflinePlayer offlinePlayer : offlinePlayers) {
			
			String offlinePlayerUUID = offlinePlayer.getUniqueId().toString();
			if(playerUUID.equalsIgnoreCase(offlinePlayerUUID)) {
				continue;
			}
			
			Block shopBlock = ShopBlock.getBlock(offlinePlayerUUID);
			if(shopBlock == null) {
				continue;
			}
			ItemStack entryItem = new ItemStack(Material.EMERALD);
			String entryName = ChatColor.DARK_GREEN + offlinePlayer.getName();
			
			MenuEntry menuEntry = new MenuEntry(entryName, entryItem, MenuAction.IGNORE, null);
			menuEntries.add(menuEntry);
		}
		
		if(menuEntries.isEmpty()) {
			player.sendMessage(bad + "There are no shops open...");
			event.setCancelled(true);
			return;
		}
		
		Menu menu = new Menu(shopCode, menuEntries);
		menu.makeMenu(player);
		
	}

}

package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Menu.Menu;
import me.Tiernanator.Menu.MenuEntry;
import me.Tiernanator.Menu.MenuEvents.MenuCloseEvent;

public class ShopUIClose implements Listener {

	private static MeconomicsMain plugin;

	public ShopUIClose(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void cleanUpSalesLoreOnMenuClose(MenuCloseEvent event) {

		Menu menu = event.getMenu();
		String menuName = menu.getMenuName();
		
		String shopCode = plugin.getConfig().getString("Shopper Code");
		if(!(menuName.contains("Shop") || menuName.contains(shopCode))) {
			return;
		}

		Logger logger = plugin.getLogger();
		logger.log(Level.INFO, "Beginning lore cleanup for menu: " + menuName);
		
		List<MenuEntry> menuEntries = menu.getMenuEntries();
		
		if(menuEntries == null) {
			logger.log(Level.INFO, "- Has no menu Entries");
			logger.log(Level.INFO, "Done.");
			return;
		}
		
		for(MenuEntry menuEntry : menuEntries) {
			
			ItemStack item = menuEntry.getEntryItem();
			logger.log(Level.INFO, "- Cleaning up lore for item: " + item.getType().name() + ":" + item.getDurability() + " x " + item.getAmount());
			ShopBlock.resolveLorePricing(item);
			
		}
		logger.log(Level.INFO, "Done.");
		
		Player player = event.getPlayer();
		Inventory menuInventory = menu.getCurrentMenu();
		for(ItemStack item : menuInventory.getContents()) {
			
			if(item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			if(ShopBlock.hasLorePricing(item)) {
				continue;
			}
			
			//If it's a menuEntry it's supposed to be there, if not, it isn't so we'll give it back :)
			MenuEntry correspondingEntry = MenuEntry.getMenuEntry(item);
			if(correspondingEntry == null) {
				player.getInventory().addItem(item);
				menuInventory.remove(item);
			}
			
		}
				
	}
	
}
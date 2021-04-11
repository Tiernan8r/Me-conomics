package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.Demand;
import me.Tiernanator.Meconomics.StockMarket.Price;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Utilities.Items.ItemUtility;
import me.Tiernanator.Utilities.Menu.Menu;
import me.Tiernanator.Utilities.Menu.MenuAction;
import me.Tiernanator.Utilities.Menu.MenuEntry;
import me.Tiernanator.Utilities.Menu.MenuEvents.MenuClickEvent;
import me.Tiernanator.Utilities.Players.PlayerLogger;

public class ShopEntryClickToBrowse implements Listener {

	@SuppressWarnings("unused")
	private static MeconomicsMain plugin;

	public ShopEntryClickToBrowse(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void openPlayersShop(MenuClickEvent event) {

		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
	
		MenuEntry menuEntry = event.getMenuEntry();
		String entryName = menuEntry.getEntryName();
		
		String colourCode = "�" + ChatColor.DARK_GREEN.getChar();		
		if(!entryName.contains(colourCode)) {
			return;
		}
		
		entryName = entryName.replace(colourCode, "");
		
		String ownerUUID = PlayerLogger.getPlayerUUIDByName(entryName);
		
		if(ownerUUID == null) {
			return;
		}
		
//		if(!ShopBlock.hasShop(ownerUUID)) {
//			return;
//		}
		
		Block block = ShopBlock.getBlock(ownerUUID);
		
		if(block == null) {
			return;
		}

		Inventory shopInventory = null;
		if(block.getState() instanceof DoubleChest) {
			DoubleChest shop = (DoubleChest) block.getState();
			shopInventory = shop.getInventory();
		} else {
			Chest shop = (Chest) block.getState();
			shopInventory = shop.getInventory();
		}
		
		List<MenuEntry> shopContents = new ArrayList<MenuEntry>();
		//The constant menu option items:
		MenuEntry closeEntry = new MenuEntry(ChatColor.RED + "Close", new ItemStack(Material.BARRIER), MenuAction.CLOSE, null, 0);
		shopContents.add(closeEntry);
		String balanceString = ChatColor.GREEN + "Your money: " + ChatColor.AQUA + "�" + String.format("%.2f", Currency.getPlayerBalance(player));
		MenuEntry playerBalance = new MenuEntry(balanceString, new ItemStack(Material.EMERALD), MenuAction.NOTHING, null, 4);
		shopContents.add(playerBalance);
		
		ItemStack[] shopItems = shopInventory.getContents();
		for(int i = 0; i < shopItems.length; i++) {
			
			ItemStack item = shopItems[i];
			
			if(item == null) {
				continue;
			}

			item = ShopBlock.resolveLorePricing(item);
			
			List<String> itemLore = new ArrayList<String>();
			if(ItemUtility.hasLore(item)) {
				itemLore = ItemUtility.getLore(item);
			}
			
			Material itemMaterial = item.getType();
			double price = Price.getPrice(itemMaterial);
			itemLore.add(ChatColor.DARK_PURPLE + "- �" + String.format("%.2f", price));
			int demand = Demand.getDailyDemand(itemMaterial);
			itemLore.add(ChatColor.DARK_PURPLE + "- " + demand + " sold today");
			ItemUtility.setLore(item, itemLore);
			
			String itemName = ItemUtility.getItemName(item);
			
			MenuEntry shopEntry = new MenuEntry(itemName, item, MenuAction.IGNORE, null, i + 9);
			shopContents.add(shopEntry);
		}
		String menuName = entryName + "'s Shop:";
		
		Menu shopMenu = new Menu(menuName, shopContents, shopInventory.getSize() + 9);
		shopMenu.makeMenu(player);
		
	}
	
}
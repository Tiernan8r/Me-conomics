package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Factions.Factions.Faction;
import me.Tiernanator.Factions.Factions.FactionAccessor;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.Demand;
import me.Tiernanator.Meconomics.StockMarket.Price;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Menu.Menu;
import me.Tiernanator.Menu.MenuEvents.MenuCloseEvent;
import me.Tiernanator.Utilities.Items.Item;
import me.Tiernanator.Utilities.Players.PlayerLogger;

public class PlayerBuy implements Listener {

	@SuppressWarnings("unused")
	private static MeconomicsMain plugin;
	private ChatColor good = Colour.GOOD.getColour();
	private ChatColor highlight = Colour.HIGHLIGHT.getColour();
	private ChatColor informative = Colour.INFORMATIVE.getColour();

	public PlayerBuy(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void playerCloseShop(MenuCloseEvent event) {

		Menu menu = event.getMenu();
		String menuName = menu.getMenuName();
		
		if(!menuName.contains("Shop")) {
			return;
		}
		
		String ownerName = menuName.replace("'s Shop:", "");
		ownerName = ownerName.replace("§" + ChatColor.DARK_PURPLE.getChar(), "");
		
		PlayerLogger playerLogger = new PlayerLogger();
		String ownerUUID = playerLogger.getPlayerUUIDByName(ownerName);
		Block shopBlock = ShopBlock.getBlock(ownerUUID);
		if(shopBlock == null) {
			return;
		}
		Chest shopChest = (Chest) shopBlock.getState();
		
		Player player = event.getPlayer();
		ItemStack[] playerInventoryContents = player.getInventory().getContents();
		List<ItemStack> soldItems = new ArrayList<ItemStack>();
		
		for(ItemStack playerItem : playerInventoryContents) {
			
			if(playerItem == null) {
				continue;
			}
			if(!Item.hasLore(playerItem)) {
				continue;
			}
			
			if(ShopBlock.hasLorePricing(playerItem)) {	
				soldItems.add(playerItem);
			}
		}
		
		double cost = 0.0;
		for(ItemStack soldItem : soldItems) {
			
			if(ShopBlock.hasLorePricing(soldItem)) {
				soldItem = ShopBlock.resolveLorePricing(soldItem);
			}
			Material material = soldItem.getType();
			double price = Price.getPrice(material);
			
			int amount = soldItem.getAmount();
			price *= amount;
			cost += price;
			
			Demand.incrementDailyDemand(material, amount);
			
		}
		//Reflect the fact that items were removed in the shop
		shopChest.update(true);
		Inventory shopInventory = shopChest.getInventory();
		Inventory menuInventory = menu.getCurrentMenu();
		
		ItemStack[] menuContents = menuInventory.getContents();
		for(int i = 9; i < menuContents.length; i++) {
			shopInventory.setItem(i - 9, menuContents[i]);
		}
		//Restore moved items from the menu row to the chest:
		for(int i = 0; i < 9; i++) {
			if(ShopBlock.hasLorePricing(menuContents[i])) {
				shopInventory.addItem(menuContents[i]);
			}
		}
		
		for(ItemStack item : menuInventory.getContents()) {
			if(item == null || item.getType() == Material.AIR) {
				continue;
			}
			ShopBlock.resolveLorePricing(item);
		}
		
		if(cost == 0.0) {
			return;
		}
		
		Currency.addToPlayerBalance(player, -cost);
		player.sendMessage(good + "The total transaction cost was: " + informative + "£" + String.format("%.2f", cost) + good + ".");
		double balance = Currency.getPlayerBalance(player);
		FactionAccessor factionAccessor = new FactionAccessor(player);
		Faction playerFaction = factionAccessor.getPlayerFaction();
//		Faction playerFaction = Faction.getPlayerFaction(player);
		player.sendMessage(good + "Your balance has decreased to " + highlight + String.format("%.2f", balance) + good + " " + playerFaction.getCurrency() + ".");
		
		Currency.addToPlayerBalance(ownerUUID, cost);
		event.setCancelled(true);
		
	}
	
}
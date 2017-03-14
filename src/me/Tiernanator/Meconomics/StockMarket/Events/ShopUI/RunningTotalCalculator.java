package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.Tiernanator.Meconomics.Main;

public class RunningTotalCalculator implements Listener {

	@SuppressWarnings("unused")
	private static Main plugin;

	public RunningTotalCalculator(Main main) {
		plugin = main;
	}

	@EventHandler
	public void recalculateCostOnInteract(InventoryClickEvent event) {

//		if(event.isCancelled()) {
//			return;
//		}
//		Inventory inventory = event.getView().getTopInventory();
//		
//		String menuName = inventory.getName();
//		if(!menuName.contains("'s Shop:")) {
//			return;
//		}
//		ItemStack totalCostDisplay = inventory.getItem(4);
//		if(totalCostDisplay == null || totalCostDisplay.getType() == Material.AIR) {
//			return;
//		}
//		MenuEntry costDisplayEntry = MenuEntry.getMenuEntry(totalCostDisplay);
//		if(costDisplayEntry == null) {
//			return;
//		}		
//		
//		Player player = (Player) event.getWhoClicked();
//		Inventory playerInventory = player.getInventory();
//		double totalCost = 0.0;
//		for(ItemStack item : playerInventory.getContents()) {
//			
//			if(item == null || item.getType() == Material.AIR) {
//				continue;
//			}
//			
//			if(!ShopBlock.hasLorePricing(item)) {
//				continue;
//			}
//			Material itemType = item.getType();
//			
//			double amount = item.getAmount();
//			double price = Price.getPrice(itemType);
//			totalCost += (price * amount);
//			
//		}
//		String costString = ChatColor.GREEN + "Cost: " + ChatColor.AQUA + "£" + String.format("%.2f", totalCost);
//		costDisplayEntry.setEntryName(costString);
//		inventory.setItem(4, totalCostDisplay);
		
	}
	
}
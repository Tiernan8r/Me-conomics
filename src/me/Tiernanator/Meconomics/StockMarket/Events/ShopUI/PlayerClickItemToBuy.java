package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.Price;

public class PlayerClickItemToBuy implements Listener {

	@SuppressWarnings("unused")
	private static MeconomicsMain plugin;
	private ChatColor bad = Colour.BAD.getColour();
	private ChatColor good = Colour.GOOD.getColour();
	private ChatColor highlight = Colour.HIGHLIGHT.getColour();
	private ChatColor informative = Colour.INFORMATIVE.getColour();

	public PlayerClickItemToBuy(MeconomicsMain main) {
		plugin = main;
	}

	@EventHandler
	public void playerSelectItemFromShop(InventoryClickEvent event) {

		if (event.isCancelled()) {
			return;
		}

		Inventory inventory = event.getInventory();

		String menuName = inventory.getName();
		if (!menuName.contains("'s Shop:")) {
			return;
		}

		InventoryAction clickAction = event.getAction();

		if (clickAction == InventoryAction.CLONE_STACK
				|| clickAction == InventoryAction.DROP_ALL_CURSOR
				|| clickAction == InventoryAction.DROP_ONE_CURSOR
				|| clickAction == InventoryAction.DROP_ALL_SLOT
				|| clickAction == InventoryAction.DROP_ONE_SLOT) {
			event.setCancelled(true);
			return;
		}

		Player player = (Player) event.getWhoClicked();
		
		ItemStack clickedItem = event.getCurrentItem();

		if (clickedItem == null) {
			return;
		}

		Material itemMaterial = clickedItem.getType();

		if (itemMaterial == Material.AIR) {
			return;
		}

		if (!inventory.contains(clickedItem)) {
			return;
		}

		double totalCost = 0.0;
		double price = Price.getPrice(itemMaterial);

		int amount = clickedItem.getAmount();
		price *= amount;
		totalCost += price;

		double playerBalance = Currency.getPlayerBalance(player);

		if (playerBalance - totalCost < 0) {
			player.sendMessage(bad + "You cannot afford this item...");
			event.setCancelled(true);
			return;
		}

		if (clickAction != InventoryAction.PLACE_ALL
				|| clickAction != InventoryAction.PLACE_ONE
				|| clickAction != InventoryAction.PLACE_SOME) {
			player.sendMessage(highlight + "" + amount + good
					+ " of this item costs: " + informative + "£"
					+ String.format("%.2f", totalCost));
		}
	}

}
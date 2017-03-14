package me.Tiernanator.Meconomics.StockMarket.Events.ShopUI;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;

import me.Tiernanator.Meconomics.Main;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;

public class HopperSuckItemFromShop implements Listener {

	@SuppressWarnings("unused")
	private static Main plugin;

	public HopperSuckItemFromShop(Main main) {
		plugin = main;
	}

	@EventHandler
	public void onHopperSuckItemFromShop(InventoryMoveItemEvent event) {

		if (event.isCancelled()) {
			return;
		}

		//So you can't pump items out of shop blocks
		Inventory inventory = event.getSource();
		Block inventoryBlock = inventory.getLocation().getBlock();
		if(ShopBlock.isShop(inventoryBlock)) {
			event.setCancelled(true);
			return;
		}
		
		//So you can't pump items into shop blocks
		inventory = event.getDestination();
		inventoryBlock = inventory.getLocation().getBlock();
		if(ShopBlock.isShop(inventoryBlock)) {
			event.setCancelled(true);
			return;
		}
		
	}

}
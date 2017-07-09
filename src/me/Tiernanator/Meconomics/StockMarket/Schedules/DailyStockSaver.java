package me.Tiernanator.Meconomics.StockMarket.Schedules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import me.Tiernanator.File.Log;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Meconomics.StockMarket.Demand;
import me.Tiernanator.Meconomics.StockMarket.Price;

public class DailyStockSaver extends BukkitRunnable {

	@SuppressWarnings("unused")
	private static MeconomicsMain plugin;

	public DailyStockSaver(MeconomicsMain main) {
		plugin = main;
	}
	
	//Break the saving up into 24 equal intervals over 24 hours...
	private int i = 0;
	//Time since 1/1/1900 in milliseconds
	private long time = System.currentTimeMillis();
	
	private int getI() {
		return i;
	}
	
	private void setI(int j) {
		i = j;
	}
	
	private void incrementI() {
		
		int j = getI();
		j++;
		if(j > 24) {
			//24 hours have passed so, reset the interval and register the new days time
			j = 0;
			time = System.currentTimeMillis();
		}
		setI(j);
		
	}
	
	private List<Material> getMaterialInterval() {
		
		List<Material> materialInterval = new ArrayList<Material>();
		Material[] allMaterials = Material.values();
		int totalMaterialsLength = allMaterials.length;
		int amountPerInterval = (int) Math.ceil(totalMaterialsLength / 24.0);
		
		for(int i = getI() * amountPerInterval ; i < (1 + getI()) * amountPerInterval; i++) {
			
			Material material = null;
			try {
				material = allMaterials[i];
			} catch (Exception e) {
				continue;
			}
			materialInterval.add(material);
			
		}
		incrementI();
		
		return materialInterval;
		
	}
	
	//the command that runs periodically that saves the demand for each item for the day to the database
	@Override
	public void run() {

		Material[] allMaterials = Material.values();
		int totalMaterialsLength = allMaterials.length;
		int amountPerInterval = (int) Math.ceil(totalMaterialsLength / 24.0);
		
		int startIndex = getI() * amountPerInterval + 1;
		int endIndex = (1 + getI()) * amountPerInterval + 1;
		
		Log log = MeconomicsMain.getLog();
		log.log("Beginning demand and price logging for entry " + startIndex + " to " + endIndex + " (" + (endIndex - startIndex) + " entries) of " + Material.values().length + ":");
		
		Date date = new Date(time);
		log.log("For the day @ time " + time + " = " + date.toString());
		
		List<Material> materialInterval = getMaterialInterval();
		for(Material material : materialInterval) {
			log.log("Entry #" + (startIndex + materialInterval.indexOf(material)) + ": " + material.name() + ":");
			
			int demand = Demand.getDailyDemand(material);
			log.log(" - Daily demand is: " + demand);
			
			//Save the Demand for the day in the database;
			Demand.addDemand(material, time, demand);
			log.log(" - Demand is now saved to database.");
			
			//Reset the demand for the day
			Demand.setDailyDemand(material, 0);
			log.log(" - Demand has been reset for the day.");
			
			double price = Price.getPrice(material);
			log.log(String.format(" - It now costs %.2f for one.", price));
			
			price = Currency.roundCurrency(price);
			//Save the Price for the day in the database;
			Price.addPrice(material, time, price);
			log.log(" - Price is now saved to database.");
			
		}
		log.log("Done demand and price logging for entries " + startIndex + "-" + endIndex + ", next interval will be in ~1 hour.");
		
	}
	
}

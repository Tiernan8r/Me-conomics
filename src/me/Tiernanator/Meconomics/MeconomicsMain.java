package me.Tiernanator.Meconomics;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.Tiernanator.Meconomics.Commands.AddMoney;
import me.Tiernanator.Meconomics.Commands.GetBalance;
import me.Tiernanator.Meconomics.Commands.SetBalance;
import me.Tiernanator.Meconomics.Events.CurrencyEventHandler;
import me.Tiernanator.Meconomics.StockMarket.Demand;
import me.Tiernanator.Meconomics.StockMarket.Price;
import me.Tiernanator.Meconomics.StockMarket.ShopBlock;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopFrontInitialiser;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopFrontRemover;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopBlock.PlayerBreakShopBlock;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopBlock.PlayerOpenShopBlock;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.HopperSuckItemFromShop;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.PlayerBuy;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.PlayerClickItemToBuy;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.RunningTotalCalculator;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.ShopEntryClickToBrowse;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.ShopUIClose;
import me.Tiernanator.Meconomics.StockMarket.Events.ShopUI.Shopper;
import me.Tiernanator.Meconomics.StockMarket.Schedules.DailyStockSaver;
import me.Tiernanator.Utilities.File.Log;
import me.Tiernanator.Utilities.SQL.SQLServer;

public class MeconomicsMain extends JavaPlugin {

	private static Log log;
	
	public static Log getLog() {
		return log;
	}
	
	private static void setLog(Log l) {
		log = l;
	}
	
	@Override
	public void onEnable() {

		Log log = new Log(this);
		setLog(log);
		
		Demand.setPlugin(this);
		Price.setPlugin(this);
		ShopBlock.setPlugin(this);
		
		initialiseSQL();
		registerCommands();
		registerEvents();
		registerTasks();
		
	}
	
	@Override
	public void onDisable() {

		log.close();

	}

	public void registerCommands() {
		getCommand("getBalance").setExecutor(new GetBalance());
		getCommand("setBalance").setExecutor(new SetBalance());
		getCommand("addMoney").setExecutor(new AddMoney());
//		getCommand("shop").setExecutor(new Shop(this));
	}
	
	public void registerEvents() {
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new CurrencyEventHandler(this), this);
		
		pm.registerEvents(new ShopFrontInitialiser(this), this);
		pm.registerEvents(new ShopFrontRemover(this), this);
		
		pm.registerEvents(new Shopper(this), this);
		pm.registerEvents(new ShopEntryClickToBrowse(this), this);
		pm.registerEvents(new ShopUIClose(this), this);

		pm.registerEvents(new PlayerClickItemToBuy(this), this);
		pm.registerEvents(new RunningTotalCalculator(this), this);
		pm.registerEvents(new PlayerBuy(this), this);

		pm.registerEvents(new HopperSuckItemFromShop(this), this);
		
		pm.registerEvents(new PlayerBreakShopBlock(this), this);
		pm.registerEvents(new PlayerOpenShopBlock(this), this);
		
	}
	
	

	private void registerTasks() {
		//in runTaskTimer() first number is how long you wait the first time to start it
		// the second is how long between iterations
		//FYI 20 ticks = 1 second and time is measured in ticks
		//There are 86400 seconds in a day == 1728000 ticks (20 ticks per second)
		//But this runs every 1/24 of a day and processes 1/24th of the entries == 72000 ticks
		DailyStockSaver stockSaver = new DailyStockSaver(this);
		stockSaver.runTaskTimerAsynchronously(this, 0, 72000);
			
	}
	
	private void initialiseSQL() {
		
		// Want to log quantity sold of material in each hour/day/week/month...
		// Probably have .yml initially, record amount put up for sale, saves to
		// database daily.
		// Same for amount sold

		String query = "CREATE TABLE IF NOT EXISTS Balance ( "
				+ "UUID varchar(36) NOT NULL ,"
				+ "Money FLOAT(2));";
		SQLServer.executeQuery(query);

		query = "CREATE TABLE IF NOT EXISTS Demand ( "
				+ "Material varchar(255) NOT NULL ,"
				+ "Date BIGINT,"
				+ "Demand int);";
		SQLServer.executeQuery(query);

		query = "CREATE TABLE IF NOT EXISTS ItemPrices ( "
				+ "Material varchar(255) NOT NULL ,"
				+ "Date BIGINT,"
				+ "Price FLOAT(2));";
		SQLServer.executeQuery(query);

		query = "CREATE TABLE IF NOT EXISTS ShopBlocks ( "
				+ "ID int NOT NULL AUTO_INCREMENT,"
				+ "World varchar(15) NOT NULL, "
				+ "X int NOT NULL, "
				+ "Y tinyint NOT NULL, "
				+ "Z int NOT NULL, "
				+ "Owner varchar(36), "
				+ "PRIMARY KEY (ID) "
				+ ");";
		SQLServer.executeQuery(query);

	}

}

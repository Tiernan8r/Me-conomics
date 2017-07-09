package me.Tiernanator.Meconomics.Events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Factions.Factions.Faction;
import me.Tiernanator.Factions.Factions.FactionAccessor;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Meconomics.MeconomicsMain;
import me.Tiernanator.Utilities.MetaData.MetaData;

public class CurrencyEventHandler implements Listener {

	private static MeconomicsMain plugin;

	ChatColor good = Colour.GOOD.getColour();
	ChatColor highlight = Colour.HIGHLIGHT.getColour();
	ChatColor warning = Colour.WARNING.getColour();

	// the partial path found in the config
	String header = "Permissions.";

	public CurrencyEventHandler(MeconomicsMain main) {
		plugin = main;
	}

	public static void setPlugin(MeconomicsMain main) {
		plugin = main;
	}
	@EventHandler
	public void applyCurrencyOnPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		if(!player.hasPlayedBefore()) {
			
			Currency.setPlayerBalance(player, 100.0);
			FactionAccessor factionAccessor = new FactionAccessor(player);
			Faction playerFaction = factionAccessor.getPlayerFaction();
//			Faction playerFaction = Faction.getPlayerFaction(player);
			
			player.sendMessage(good + "As it is your first time on the server, you have been given a complimentary " + highlight + "100 " + good + playerFaction.getCurrency() + ".");
			return;
		} else {
			double amount = Currency.getPlayerBalance(player);
			Currency.setPlayerBalance(player, amount);
			return;
		}
	}

	@EventHandler
	public void registerAmountOnPlayerLeave(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		int balance = 0;
		try {
			balance = (int) MetaData.getMetadata(player, "Money", plugin);
			Currency.setPlayerBalance(player, balance);
		} catch (Exception e) {
		}

	}
}

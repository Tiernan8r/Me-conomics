package me.Tiernanator.Meconomics.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Factions.Factions.Faction;
import me.Tiernanator.Factions.Factions.FactionAccessor;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Utilities.Players.GetPlayer;

public class AddMoney implements CommandExecutor {
	
	//I told you they recurred...(I did in TestPermission anyway)
	private static ChatColor highlight = Colour.HIGHLIGHT.getColour();
	private static ChatColor warning = Colour.WARNING.getColour();
	private static ChatColor good = Colour.GOOD.getColour();
	private static ChatColor informative = Colour.INFORMATIVE.getColour();
	
	
	// this has to stay the Main class won't be happy.
	public AddMoney() {
	}

	//this Command Sends the player a message with their Group display name.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(args.length < 2) {
			sender.sendMessage(warning + "You must specify a player and an amount.");
			return false;
		}
		
		
		Player playerForCurrency = GetPlayer.getPlayer(args[0], sender, warning, highlight);

		if(playerForCurrency == null) {
			return false;
		}
		if(!sender.isOp()) {
			sender.sendMessage(warning + "You can't use this command");
			return false;
		}
		if(args.length < 2) {
			sender.sendMessage(warning + "you must specify a player and an amount!");
			return false;
		}
		
		double amount = Double.parseDouble(args[1]);
		
		addMoney(playerForCurrency, amount);

		FactionAccessor factionAccessor = new FactionAccessor(playerForCurrency);
		Faction playerFaction = factionAccessor.getPlayerFaction();
//		Faction playerFaction = Faction.getPlayerFaction(playerForCurrency);
		
		double balance = Currency.getPlayerBalance(playerForCurrency);
		sender.sendMessage(highlight + playerForCurrency.getName() + good + " has been given " + informative + "+" + String.format("%.2f", amount) + good + " " + playerFaction.getCurrency() + ".");
		
		String change = "increased";
		if(amount < 1) {
			change = "decreased";
		}
		
		playerForCurrency.sendMessage(good + "Your balance has " + change + " to " + highlight + String.format("%.2f", balance) + good + " " + playerFaction.getCurrency() + ".");


		return true;
	}

	public static void addMoney(Player player, double amount) {
		
		double current = Currency.getPlayerBalance(player);
		current += amount;
		
		Currency.setPlayerBalance(player, current);

	}
	
}

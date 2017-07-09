package me.Tiernanator.Meconomics.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Tiernanator.Colours.Colour;
import me.Tiernanator.Factions.Factions.Faction;
import me.Tiernanator.Factions.Factions.FactionAccessor;
import me.Tiernanator.Meconomics.Currency;

public class GetBalance implements CommandExecutor {

	// group arrays
	static List<String> groups;
	
	//I told you they recurred...(I did in TestPermission anyway)
	private static ChatColor good;
	private static ChatColor bad;
	private static ChatColor highlight;
	private static ChatColor informative;
	private static ChatColor warning;
	
	public GetBalance() {
	}

	//this Command Sends the player a message with their Group display name.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// set the constants
		allocateColours();

		//only Players have permissions...
		if(!(sender instanceof Player)) {
			sender.sendMessage(warning + "Only players can use this command.");
			return false;
		}
		// get the player
		Player player = (Player) sender;
		//uses the getGroup() function found below
		double amount = Currency.getPlayerBalance(player);
		if(Double.toString(amount) == null) {
			player.sendMessage(bad + "You don't have any money...");
			return false;
		
		}
		if(amount == 0) {
			player.sendMessage(bad + "You don't have any money...");
			return false;
		
		}
		FactionAccessor factionAccessor = new FactionAccessor(player);
		Faction playerFaction = factionAccessor.getPlayerFaction();
//		Faction playerFaction = Faction.getPlayerFaction(player);
		
		if(amount == 69) {
			player.sendMessage(informative + "Tee Hee, " + highlight + String.format("%.2f", amount) + informative + " " + playerFaction.getCurrency() + ".");
			return true;
		
		}
		player.sendMessage(good + "Your balance is: " + highlight + String.format("%.2f", amount) + good + " " + playerFaction.getCurrency() + ".");
		return true;
	}

	// constants initialising
	private static void allocateColours() {
			
		warning = Colour.WARNING.getColour();
		informative = Colour.INFORMATIVE.getColour();
		highlight = Colour.HIGHLIGHT.getColour();
		good = Colour.GOOD.getColour();
		bad = Colour.BAD.getColour();
		
	}
}

package me.Tiernanator.Meconomics.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Tiernanator.Factions.Factions.Faction;
import me.Tiernanator.Factions.Factions.FactionAccessor;
import me.Tiernanator.Meconomics.Currency;
import me.Tiernanator.Utilities.Colours.Colour;
import me.Tiernanator.Utilities.Players.GetPlayer;

public class SetBalance implements CommandExecutor {

	// I told you they recurred...(I did in TestPermission anyway)
	private static ChatColor warning = Colour.WARNING.getColour();
	private static ChatColor informative = Colour.INFORMATIVE.getColour();
	private static ChatColor highlight = Colour.INFORMATIVE.getColour();
	private static ChatColor good = Colour.GOOD.getColour();

	// this has to stay the Main class won't be happy.
	public SetBalance() {
	}

	// this Command Sends the player a message with their Group display name.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (args.length < 2) {
			sender.sendMessage(
					warning + "You must specify a player and an amount.");
			return false;
		}

		Player playerForCurrency = GetPlayer.getPlayer(args[0], sender, warning, highlight);

		if (playerForCurrency == null) {
			return false;
		}
		if (!sender.isOp()) {
			sender.sendMessage(warning + "You can't use this command");
			return false;
		}

		double amount = Double.parseDouble(args[1]);
		Currency.setPlayerBalance(playerForCurrency, amount);
		
		FactionAccessor factionAccessor = new FactionAccessor(playerForCurrency);
		Faction playerFaction = factionAccessor.getPlayerFaction();
//		Faction playerFaction = Faction.getPlayerFaction(playerForCurrency);
		sender.sendMessage(
				highlight + playerForCurrency.getName() + good
						+ "'s balance has been set to " + informative
						+ String.format("%.2f", amount) + good + " "
						+ playerFaction.getCurrency()
						+ ".");
		double playerBalance = Currency.getPlayerBalance(playerForCurrency);
		playerForCurrency
				.sendMessage(good + "Your balance is now " + highlight
						+ String.format("%.2f", playerBalance) + good + " "
						+ playerFaction.getCurrency()
						+ ".");

		return true;
	}

}

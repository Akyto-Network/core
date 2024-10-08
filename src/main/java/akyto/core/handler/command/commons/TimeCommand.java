package akyto.core.handler.command.commons;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import akyto.core.Core;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

public class TimeCommand {
	
	private final Core main = Core.API;
	
	@Command(name = "day", aliases= {"night", "sunset"}, inGameOnly = true)
	public boolean timeCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final org.bukkit.command.Command cmd = arg.getCommand();
		if (!(sender instanceof Player)) return false;
		if (cmd.getName().equalsIgnoreCase("night")) Bukkit.getPlayer(sender.getName()).setPlayerTime(18000, false);
		if (cmd.getName().equalsIgnoreCase("day")) Bukkit.getPlayer(sender.getName()).setPlayerTime(12500, true);
		if (cmd.getName().equalsIgnoreCase("sunset")) Bukkit.getPlayer(sender.getName()).setPlayerTime(0, true);
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getTime().replace("%time%", cmd.getName()));
		return false;
	}

}

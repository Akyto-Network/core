package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;

public class TimeCommand implements CommandExecutor {
	
	private Core main;

	public TimeCommand(final Core main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		if (cmd.getName().equalsIgnoreCase("night")) Bukkit.getPlayer(sender.getName()).setPlayerTime(18000, false);
		if (cmd.getName().equalsIgnoreCase("day")) Bukkit.getPlayer(sender.getName()).setPlayerTime(12500, true);
		if (cmd.getName().equalsIgnoreCase("sunset")) Bukkit.getPlayer(sender.getName()).setPlayerTime(0, true);
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getTime().replace("%time%", cmd.getName()));
		return false;
	}

}

package gym.core.handler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class SocialsCommand {
	
	@Command(name = "discord", aliases= {"socials", "ts", "namemc", "social"}, inGameOnly = true)
	public boolean timeCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		sender.sendMessage(new String[] {
				ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------------",
				ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "Discord: " + ChatColor.WHITE + "discord.akyto.club",
				ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "NameMC: " + ChatColor.WHITE + "namemc.com/server/akyto.club",
				ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------------"
				});
		return false;
	}

}

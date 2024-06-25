package akyto.core.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

public class AnnounceCommand {
	
	@Command(name = "announce", aliases= {"bc", "broadcast", "announcement"}, permission = "akyto.admin")
	public void announceCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		String announce = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : "Broadcast as made!";
		Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Announce" + ChatColor.GRAY + ":");
		Bukkit.broadcastMessage(ChatColor.ITALIC + ChatColor.translate(announce));
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GRAY + "Author: " + ChatColor.RED + ChatColor.ITALIC + sender.getName());
		Bukkit.broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		sender.sendMessage(ChatColor.GREEN + "Your announce has been sent!");
    }
}

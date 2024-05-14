package gym.core.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class AnnounceCommand {
	
	@Command(name = "announce", aliases= {"bc", "broadcast", "announcement"}, permission = "akyto.admin")
	public void announceCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		String announce = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : "Broadcast as made!"; 
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + ChatColor.BOLD + "⚠" + ChatColor.GRAY + "] " + ChatColor.GOLD + announce);
		Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		Bukkit.broadcastMessage(" ");
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1f, 1f));
		sender.sendMessage(ChatColor.GREEN + "Your announce has been sent!");
    }
}

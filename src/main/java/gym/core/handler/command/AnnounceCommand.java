package gym.core.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnounceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("akyto.admin")) {
			sender.sendMessage(ChatColor.RED + "You do not have the required permission!");
			return false;
		}
		String announce = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : "Broadcast as maded!"; 
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + ChatColor.BOLD + "⚠" + ChatColor.GRAY + "] " + ChatColor.GOLD + announce);
		Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------------------");
		Bukkit.broadcastMessage(" ");
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1f, 1f));
		sender.sendMessage(ChatColor.GREEN + "Your announce has been sent!");
		return false;
	}

}

package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BulldogCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) return false;
		if (Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(ChatColor.RED + "Victim isn't online!");
			return false;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "/bulldog <player>");
			return false;
		}
		for (int i = 0; i < 99; i++) {
			Bukkit.getPlayer(args[0]).playSound(Bukkit.getPlayer(args[0]).getLocation(), Sound.GHAST_MOAN, 1f, 1f);
			Bukkit.getPlayer(args[0]).playSound(Bukkit.getPlayer(args[0]).getLocation(), Sound.ANVIL_LAND, 1f, 1f);
			Bukkit.getPlayer(args[0]).playSound(Bukkit.getPlayer(args[0]).getLocation(), Sound.ANVIL_USE, 1f, 1f);
		}
		return false;
	}

}

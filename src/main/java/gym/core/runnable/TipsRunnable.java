package gym.core.runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class TipsRunnable extends BukkitRunnable {
	
	private int counter;
	private boolean first = true;

	@Override
	public void run() {
		counter++;
		if (first) {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.WHITE + "Making C.P.S too high can result in a ban so avoid :p"));
			first = false;
		}
		if (counter == 1) {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.WHITE + "Don't forget to join our discord server " + ChatColor.RED + "discord.akyto.club"));
		}
		if (counter == 2) {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.WHITE + "Vote for us on NameMC: " + ChatColor.RED + "namemc.com/server/akyto.club"));
		}
		if (counter == 3) {
			Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.WHITE + "The server is still under development if you see any bugs report them to us and don't abuse them :3"));
			counter = 0;
		}
	}
	
	

}

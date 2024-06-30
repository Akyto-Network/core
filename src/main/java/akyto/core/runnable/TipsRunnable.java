package akyto.core.runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class TipsRunnable extends BukkitRunnable {

	private int idx = 0;
	private final String tips = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.RESET;
	private final String reminder = ChatColor.GRAY + "[" + ChatColor.RED + "Reminder" + ChatColor.GRAY + "] " + ChatColor.RESET;
	private final List<String> messages = Arrays.asList(
			reminder + "High CPS can result to a ban.",
			tips + "Join our discord server. Everything happens there" + ChatColor.RED + "discord.akyto.club",
			tips + "Like us on NameMC: " + ChatColor.RED + "namemc.com/server/akyto.club",
			reminder + "Akyto is still under development. Expect to play with some bugs at the moment."
	);

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(messages.get(idx)));
		idx = (idx + 1) % messages.size();
	}
	
	

}

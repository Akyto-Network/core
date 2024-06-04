package gym.core.runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class TipsRunnable extends BukkitRunnable {

	private int idx = 0;
	private final String prefix = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Tips" + ChatColor.GRAY + "] " + ChatColor.RESET;
	private final List<String> messages = Arrays.asList(
			prefix + "Making C.P.S too high can result in a ban so avoid :p",
			prefix + "Don't forget to join our discord server " + ChatColor.RED + "discord.akyto.club",
			prefix + "Vote for us on NameMC: " + ChatColor.RED + "namemc.com/server/akyto.club",
			prefix + "The server is still under development if you see any bugs report them to us and don't abuse them :3"
	);

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(messages.get(idx)));
		idx = (idx + 1) % messages.size();
	}
	
	

}

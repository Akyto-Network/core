package gym.core.runnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import gym.core.handler.command.ViewCpsCommand;
import gym.core.profile.Profile;
import net.md_5.bungee.api.ChatColor;

public class VerifRunnable extends BukkitRunnable {

	@Override
	public void run() {
		for(Player verifier : ViewCpsCommand.verifiers.keySet()){
			if (verifier.getOpenInventory().getTopInventory() != null && verifier.getOpenInventory().getTopInventory().getTitle().startsWith(ChatColor.GRAY + "Viewing CPS »")){
				String o = verifier.getOpenInventory().getTopInventory().getName().split("» ")[1];
	            if (Bukkit.getPlayer(o) != null) {
	              Profile wp = ViewCpsCommand.verifiers.get(verifier);
	              
	              ItemStack max = new ItemStack(Material.PAINTING, Math.min(wp.clicks[0], 64));
	              ItemMeta maxm = max.getItemMeta();
	              maxm.setDisplayName(ChatColor.GRAY + "Maximum Click: " + ChatColor.RED + wp.maxClick);
	              maxm.setLore(Collections.singletonList(ChatColor.GRAY + "Instant Click: " + ChatColor.DARK_AQUA + wp.clicks[0]));
	              max.setItemMeta(maxm);
	              
	              int ping = Bukkit.getPlayer(wp.getUuid()).getPing();
	              ItemStack latency = new ItemStack(Material.IRON_BLOCK, Math.min(ping, 64));
	              ItemMeta latencyMeta = latency.getItemMeta();
	              latencyMeta.setDisplayName(ChatColor.YELLOW + "Latency: " + ChatColor.RED + ping);
		    	  latency.setItemMeta(latencyMeta);
	              
	              ItemStack punishment = new ItemStack(Material.BOOK);
	              ItemMeta punishmentMeta = punishment.getItemMeta();
	              punishmentMeta.setDisplayName(ChatColor.RED + "Punish " + o);
	              punishmentMeta.setLore(Arrays.asList(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------", ChatColor.GREEN + "Click here to punish him", ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------"));
	              punishment.setItemMeta(punishmentMeta);

				  ItemStack last = getLast(wp);

				  verifier.getOpenInventory().getTopInventory().setItem(0, max);
		          verifier.getOpenInventory().getTopInventory().setItem(1, new ItemStack(Material.STAINED_GLASS_PANE));
		          verifier.getOpenInventory().getTopInventory().setItem(2, last);
		          verifier.getOpenInventory().getTopInventory().setItem(3, new ItemStack(Material.STAINED_GLASS_PANE));
		          verifier.getOpenInventory().getTopInventory().setItem(4, new ItemStack(Material.STAINED_GLASS_PANE));
		          verifier.getOpenInventory().getTopInventory().setItem(5, new ItemStack(Material.STAINED_GLASS_PANE));
		          verifier.getOpenInventory().getTopInventory().setItem(6, punishment);
		          verifier.getOpenInventory().getTopInventory().setItem(7, new ItemStack(Material.STAINED_GLASS_PANE));
		          verifier.getOpenInventory().getTopInventory().setItem(8, latency);
	            }
	            continue;
			}
			ViewCpsCommand.verifiers.remove(verifier);
		}
	}

	private static ItemStack getLast(Profile wp) {
		ItemStack last = new ItemStack(Material.PAPER, Math.min(wp.clicks[1], 64));
		ItemMeta lastMeta = last.getItemMeta();
		lastMeta.setDisplayName(ChatColor.GRAY + "Last 5seconds CPS:");
		final List<String> lore = Lists.newArrayList();
		for (int i = 1; i < wp.clicks.length; i++) {
			lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + " * " + ChatColor.WHITE + wp.clicks[i]);
		}
		lastMeta.setLore(lore);
		last.setItemMeta(lastMeta);
		return last;
	}
}

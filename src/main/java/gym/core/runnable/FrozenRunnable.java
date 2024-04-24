package gym.core.runnable;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import gym.core.Core;

public class FrozenRunnable extends BukkitRunnable {
	
	private Core main;
	private UUID uuid;
	
	public FrozenRunnable(final Core main, final UUID uuid) {
		this.main = main;
		this.uuid = uuid;
	}

	@Override
	public void run() {
		if (Bukkit.getPlayer(uuid) != null) {
			if (!this.main.getManagerHandler().getProfileManager().getFrozed().contains(uuid)) {
				Bukkit.getPlayer(uuid).closeInventory();
				this.cancel();
				return;
			}
			Bukkit.getPlayer(uuid).openInventory(this.main.getManagerHandler().getInventoryManager().getFrozeInventory());	
		}
	}
	

}

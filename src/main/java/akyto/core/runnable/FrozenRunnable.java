package akyto.core.runnable;

import akyto.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FrozenRunnable extends BukkitRunnable {
	
	private final Core main;
	private final UUID uuid;
	
	public FrozenRunnable(final Core main, final UUID uuid) {
		this.main = main;
		this.uuid = uuid;
	}

	@Override
	public void run() {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null || !player.isOnline() || !this.main.getManagerHandler().getProfileManager().getFrozen().contains(uuid)) {
			if (player != null)
				player.closeInventory();
			this.cancel();
			return;
		}

		player.openInventory(this.main.getManagerHandler().getInventoryManager().getFrozeInventory());
	}
}

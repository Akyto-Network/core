package gym.core.handler.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;

import gym.core.Core;
import gym.core.punishment.BanEntry;
import gym.core.punishment.MuteEntry;
import gym.core.punishment.WarnEntry;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Maps;

@Getter
public class PunishmentManager {
	
	private Core main;
	private ConcurrentMap<UUID, BanEntry> banned = Maps.newConcurrentMap();
	private List<UUID> unbanned = new ArrayList<UUID>();
	private ConcurrentMap<UUID, MuteEntry> muted = Maps.newConcurrentMap();
	private List<UUID> unmuted = new ArrayList<UUID>();
	private ConcurrentMap<UUID, WarnEntry> warned = Maps.newConcurrentMap();
	private List<UUID> unwarned = new ArrayList<UUID>();
	
	public PunishmentManager(final Core main) {	
		this.main = main;
	}
	
	public void addBan(final UUID banned, final String expires, final String reason, final String judge) {
		this.banned.put(banned, new BanEntry(expires, reason, judge));
		if (Bukkit.getPlayer(banned) != null) Bukkit.getPlayer(banned).kickPlayer(this.main.getLoaderHandler().getMessage().getBanDisconnect().replace("%expires%", expires).replace("%reason%", reason).replace("%judge%", judge));
	}
	
	public void addMute(final UUID mutted, final String expires, final String reason, final String judge) {
		this.muted.put(mutted, new MuteEntry(expires, reason, judge));
	}
	
	public void removeMute(final UUID mutted) {
		this.muted.remove(mutted);
		this.unmuted.add(mutted);
	}
	
	public void removeBan(final UUID banned) {
		this.banned.remove(banned);
		this.unbanned.add(banned);
	}

}

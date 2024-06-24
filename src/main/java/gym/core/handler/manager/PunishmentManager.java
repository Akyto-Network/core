package gym.core.handler.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;

import gym.core.Core;
import gym.core.punishment.PunishmentType;
import gym.core.punishment.cache.BanEntry;
import gym.core.punishment.cache.MuteEntry;
import gym.core.punishment.cache.WarnEntry;
import lombok.Getter;
import com.google.common.collect.Maps;

@Getter
public class PunishmentManager {
	
	private final Core main;
	private final ConcurrentMap<UUID, BanEntry> banned = Maps.newConcurrentMap();
	private final List<UUID> unbanned = new ArrayList<>();
	private final ConcurrentMap<UUID, MuteEntry> muted = Maps.newConcurrentMap();
	private final List<UUID> unmuted = new ArrayList<>();
	private final ConcurrentMap<UUID, WarnEntry> warned = Maps.newConcurrentMap();
	private final List<UUID> unwarned = new ArrayList<>();
	
	public PunishmentManager(final Core main) {	
		this.main = main;
	}
	
	public void addPunishment(final UUID victim, final String expires, final String reason, final String judge, final PunishmentType type) {
		if (type.equals(PunishmentType.BAN)) {
			this.banned.put(victim, new BanEntry(expires, reason, judge));
			if (Bukkit.getPlayer(victim) != null) Bukkit.getPlayer(victim).kickPlayer(this.main.getLoaderHandler().getMessage().getBanDisconnect().replace("%expires%", expires).replace("%reason%", reason).replace("%judge%", judge));	
		}
		if (type.equals(PunishmentType.MUTE)) {
			this.muted.put(victim, new MuteEntry(expires, reason, judge));
		}
	}
	
	public void removePunishment(final UUID target, final PunishmentType type) {
		if (type.equals(PunishmentType.MUTE)) {
			this.muted.remove(target);
			this.unmuted.add(target);
		}
		if (type.equals(PunishmentType.BAN)) {
			this.banned.remove(target);
			this.unbanned.add(target);
		}
	}
}

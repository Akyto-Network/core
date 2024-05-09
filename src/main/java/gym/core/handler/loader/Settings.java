package gym.core.handler.loader;

import gym.core.Core;
import lombok.Getter;

@Getter
public class Settings {
	
	private final boolean rankPromoteBroad;
	private final boolean rankDemoteBroad;
	private final boolean banBroad;
	private final boolean muteBroad;
	private final boolean warnBroad;
	private final boolean tryToConnect;
	private final boolean staffNotifications;
	private final boolean bungeeCord;
	private final boolean chatCooldown;
	private final int chatCooldownTime;
	private final boolean freezeInventory;
	private final boolean namemcCheck;
	private final String hubInstance;
	
	public Settings(final Core main) {
		this.rankPromoteBroad = main.getConfig().getBoolean("broadcast-settings.rank-promote");
		this.rankDemoteBroad = main.getConfig().getBoolean("broadcast-settings.rank-demote");
		this.banBroad = main.getConfig().getBoolean("broadcast-settings.banAnnounce");
		this.muteBroad = main.getConfig().getBoolean("broadcast-settings.muteAnnounce");
		this.warnBroad = main.getConfig().getBoolean("broadcast-settings.warnAnnounce");
		this.tryToConnect = main.getConfig().getBoolean("broadcast-settings.try-to-connect-notifications");
		this.staffNotifications = main.getConfig().getBoolean("broadcast-settings.staff-proxy-notification");
		this.bungeeCord = main.getConfig().getBoolean("bungeecord.enable");
		this.chatCooldown = main.getConfig().getBoolean("chat.cooldown.enabled");
		this.chatCooldownTime = main.getConfig().getInt("chat.cooldown.time");
		this.freezeInventory = main.getConfig().getBoolean("freeze.open-inventory");
		this.namemcCheck = main.getConfig().getBoolean("namemc.check-enabled");
		this.hubInstance = main.getConfig().getString("bungeecord.hub-instance");
	}

}

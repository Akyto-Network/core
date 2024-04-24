package gym.core.handler.loader;

import gym.core.Core;
import lombok.Getter;

@Getter
public class Settings {
	
	private boolean rankPromoteBroad;
	private boolean rankDemoteBroad;
	private boolean banBroad;
	private boolean muteBroad;
	private boolean warnBroad;
	private boolean tryToConnect;
	private boolean staffNotifications;
	private boolean bungeeCord;
	private boolean chatCooldown;
	private int chatCooldownTime;
	private boolean freezeInventory;
	
	public Settings(final Core main) {
		this.rankPromoteBroad = main.getConfig().getString("broadcast-settings.rank-promote").equals("true") ? true : false;
		this.rankDemoteBroad = main.getConfig().getString("broadcast-settings.rank-demote").equals("true") ? true : false;
		this.banBroad = main.getConfig().getString("broadcast-settings.banAnnounce").equals("true") ? true : false;
		this.muteBroad = main.getConfig().getString("broadcast-settings.muteAnnounce").equals("true") ? true : false;
		this.warnBroad = main.getConfig().getString("broadcast-settings.warnAnnounce").equals("true") ? true : false;
		this.tryToConnect = main.getConfig().getString("broadcast-settings.try-to-connect-notifications").equals("true") ? true : false;
		this.staffNotifications = main.getConfig().getString("broadcast-settings.staff-proxy-notification").equals("true") ? true : false;
		this.bungeeCord = main.getConfig().getString("bungeecord.enable").equals("true") ? true : false;
		this.chatCooldown = main.getConfig().getString("chat.cooldown.enabled").equals("true") ? true : false;
		this.chatCooldownTime = main.getConfig().getInt("chat.cooldown.time");
		this.freezeInventory = main.getConfig().getString("freeze.open-inventory").equals("true") ? true : false;
		
	}

}

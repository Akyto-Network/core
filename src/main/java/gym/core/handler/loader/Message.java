package gym.core.handler.loader;

import java.util.List;

import com.google.common.collect.Lists;

import gym.core.Core;
import gym.core.utils.Utils;
import lombok.Getter;

@Getter
public class Message {
	
	private String noPermission;
	private String created;
	private String deleted;
	private String alreadyExist;
	private String notExist;
	private String edited;
	private String alreadySet;
	private String neverPlayed;
	private String staffAnnounce;
	private String rankUp;
	private String rankDown;
	private String chatFormat;
	private String banDisconnect;
	private String banAnnounce;
	private String muteAnnounce;
	private String unmuteAnnounce;
	private String muteCancel;
	private String unbanAnnounce;
	private String tryToConnect;
	private String kickWhitelistProxy;
	private String chatCooldown;
	private String chatOpenned;
	private String chatClosed;
	private String chatPriorityChange;
	private String frozed;
	private String frozeStatus;
	private List<String> movementFrozeMessage = Lists.newArrayList();
	private List<String> punishmentInfo = Lists.newArrayList();
	private List<String> muteHelp = Lists.newArrayList();
	private List<String> banHelp = Lists.newArrayList();
	private List<String> rankHelp = Lists.newArrayList();
	private List<String> bungeeIps = Lists.newArrayList();
	private List<String> filteredText = Lists.newArrayList();
	
	public Message(final Core main) {
		this.banDisconnect = Utils.translate(main.getConfig().getString("messages.banDisconnect"));
		this.noPermission = Utils.translate(main.getConfig().getString("messages.noPermission"));
		this.created = Utils.translate(main.getConfig().getString("messages.created"));
		this.deleted = Utils.translate(main.getConfig().getString("messages.deleted"));
		this.notExist = Utils.translate(main.getConfig().getString("messages.notExist"));
		this.edited = Utils.translate(main.getConfig().getString("messages.edited"));
		this.alreadyExist = Utils.translate(main.getConfig().getString("messages.alreadyExist"));
		this.alreadySet = Utils.translate(main.getConfig().getString("messages.alreadySet"));
		this.neverPlayed = Utils.translate(main.getConfig().getString("messages.neverPlayed"));
		this.staffAnnounce = Utils.translate(main.getConfig().getString("messages.staff-proxy-notification"));
		this.rankUp = Utils.translate(main.getConfig().getString("messages.rank-promote"));
		this.rankDown = Utils.translate(main.getConfig().getString("messages.rank-demote"));
		this.banAnnounce = Utils.translate(main.getConfig().getString("messages.banAnnounce"));
		this.unbanAnnounce = Utils.translate(main.getConfig().getString("messages.unbanAnnounce"));
		this.muteAnnounce = Utils.translate(main.getConfig().getString("messages.muteAnnounce"));
		this.unmuteAnnounce = Utils.translate(main.getConfig().getString("messages.unmuteAnnounce"));
		this.muteCancel = Utils.translate(main.getConfig().getString("messages.muteCancel"));
		this.tryToConnect = Utils.translate(main.getConfig().getString("messages.banTryConnect"));
		this.chatFormat = Utils.translate(main.getConfig().getString("chat.format"));
		this.kickWhitelistProxy = Utils.translate(main.getConfig().getString("bungeecord.kick-whitout-using-correct-ip"));
		this.chatCooldown = Utils.translate(main.getConfig().getString("chat.cooldown.message"));
		this.chatOpenned = Utils.translate(main.getConfig().getString("messages.chatOpenned"));
		this.chatClosed = Utils.translate(main.getConfig().getString("messages.chatClosed"));
		this.chatPriorityChange = Utils.translate(main.getConfig().getString("messages.chatPriorityChanged"));
		this.frozed = Utils.translate(main.getConfig().getString("messages.frozed"));
		this.frozeStatus = Utils.translate(main.getConfig().getString("messages.frozeStatusNotif"));
		main.getConfig().getStringList("bungeecord.whitelist").forEach(str -> this.bungeeIps.add(str));
		main.getConfig().getStringList("messages.punishmentInformations").forEach(str -> this.punishmentInfo.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.muteHelp").forEach(str -> this.muteHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.banHelp").forEach(str -> this.banHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.rankHelp").forEach(str -> this.rankHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("chat.filtered").forEach(str -> this.filteredText.add(Utils.translate(str)));
		main.getConfig().getStringList("freeze.movement-message").forEach(str -> this.movementFrozeMessage.add(Utils.translate(str)));
	}

}

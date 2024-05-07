package gym.core.handler.loader;

import java.util.List;

import com.google.common.collect.Lists;

import gym.core.Core;
import gym.core.utils.Utils;
import lombok.Getter;

@Getter
public class Message {
	
	private final String noPermission;
	private final String created;
	private final String deleted;
	private final String alreadyExist;
	private final String notExist;
	private final String edited;
	private final String alreadySet;
	private final String neverPlayed;
	private final String staffAnnounce;
	private final String rankUp;
	private final String rankDown;
	private final String chatFormat;
	private final String banDisconnect;
	private final String banAnnounce;
	private final String muteAnnounce;
	private final String unmuteAnnounce;
	private final String muteCancel;
	private final String unbanAnnounce;
	private final String tryToConnect;
	private final String kickWhitelistProxy;
	private final String chatCooldown;
	private final String chatOpened;
	private final String chatClosed;
	private final String chatPriorityChange;
	private final String frozed;
	private final String frozeStatus;
	private final String enterModMode;
	private final String time;
	private final String pmFormat;
	private final String scSymbol;
	private final String scFormat;
	private final String nameMCLike;
	private final String nameMCUnlike;
	private final String nameMCLikeTag;
	private final List<String> movementFrozeMessage = Lists.newArrayList();
	private final List<String> punishmentInfo = Lists.newArrayList();
	private final List<String> muteHelp = Lists.newArrayList();
	private final List<String> banHelp = Lists.newArrayList();
	private final List<String> rankHelp = Lists.newArrayList();
	private final List<String> bungeeIps = Lists.newArrayList();
	private final List<String> filteredText = Lists.newArrayList();
	private final List<String> randomTeleport = Lists.newArrayList();
	
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
		this.chatOpened = Utils.translate(main.getConfig().getString("messages.chatOpenned"));
		this.chatClosed = Utils.translate(main.getConfig().getString("messages.chatClosed"));
		this.chatPriorityChange = Utils.translate(main.getConfig().getString("messages.chatPriorityChanged"));
		this.frozed = Utils.translate(main.getConfig().getString("messages.frozed"));
		this.frozeStatus = Utils.translate(main.getConfig().getString("messages.frozeStatusNotif"));
		this.enterModMode = Utils.translate(main.getConfig().getString("messages.modmode"));
		this.time = Utils.translate(main.getConfig().getString("messages.time"));
		this.pmFormat = Utils.translate(main.getConfig().getString("messages.private-message"));
		this.scFormat = Utils.translate(main.getConfig().getString("chat.staff-format"));
		this.scSymbol = main.getConfig().getString("chat.staff-symbol");
		this.nameMCLike = Utils.translate(main.getConfig().getString("messages.namemc-liked"));
		this.nameMCUnlike = Utils.translate(main.getConfig().getString("messages.namemc-unliked"));
		this.nameMCLikeTag = Utils.translate(main.getConfig().getString("namemc.like-tag"));
        this.bungeeIps.addAll(main.getConfig().getStringList("bungeecord.whitelist"));
		main.getConfig().getStringList("messages.punishmentInformations").forEach(str -> this.punishmentInfo.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.muteHelp").forEach(str -> this.muteHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.banHelp").forEach(str -> this.banHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.rankHelp").forEach(str -> this.rankHelp.add(Utils.translate(str)));
		main.getConfig().getStringList("chat.filtered").forEach(str -> this.filteredText.add(Utils.translate(str)));
		main.getConfig().getStringList("freeze.movement-message").forEach(str -> this.movementFrozeMessage.add(Utils.translate(str)));
		main.getConfig().getStringList("messages.randomTeleport").forEach(str -> this.randomTeleport.add(Utils.translate(str)));
	}

}

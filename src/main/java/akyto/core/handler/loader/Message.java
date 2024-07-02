package akyto.core.handler.loader;

import java.util.List;

import com.google.common.collect.Lists;

import akyto.core.Core;
import akyto.core.utils.CoreUtils;
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
	private final String blacklistDisconnect;
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
	private final String clickCancel;
	private final String whitelistDisabled;
	private final String whitelistEnabled;
	private final String whitelistAdd;
	private final String whitelistRemove;
	private final String whitelistBlacklist;
	private final String whitelistKickOnList;
	private final String whitelistKickRank;
	private final String whitelistKickBlacklist;
	private final String blacklistAnnounce;
	private final String unblacklistAnnounce;
	private final List<String> movementFrozeMessage = Lists.newArrayList();
	private final List<String> punishmentInfo = Lists.newArrayList();
	private final List<String> muteHelp = Lists.newArrayList();
	private final List<String> banHelp = Lists.newArrayList();
	private final List<String> rankHelp = Lists.newArrayList();
	private final List<String> bungeeIps = Lists.newArrayList();
	private final List<String> filteredText = Lists.newArrayList();
	private final List<String> randomTeleport = Lists.newArrayList();
	
	public Message(final Core main) {
		this.banDisconnect = CoreUtils.translate(main.getConfig().getString("messages.banDisconnect"));
		this.blacklistDisconnect = CoreUtils.translate(main.getConfig().getString("messages.blacklistDisconnect"));
		this.noPermission = CoreUtils.translate(main.getConfig().getString("messages.noPermission"));
		this.created = CoreUtils.translate(main.getConfig().getString("messages.created"));
		this.deleted = CoreUtils.translate(main.getConfig().getString("messages.deleted"));
		this.notExist = CoreUtils.translate(main.getConfig().getString("messages.notExist"));
		this.edited = CoreUtils.translate(main.getConfig().getString("messages.edited"));
		this.alreadyExist = CoreUtils.translate(main.getConfig().getString("messages.alreadyExist"));
		this.alreadySet = CoreUtils.translate(main.getConfig().getString("messages.alreadySet"));
		this.neverPlayed = CoreUtils.translate(main.getConfig().getString("messages.neverPlayed"));
		this.staffAnnounce = CoreUtils.translate(main.getConfig().getString("messages.staff-proxy-notification"));
		this.rankUp = CoreUtils.translate(main.getConfig().getString("messages.rank-promote"));
		this.rankDown = CoreUtils.translate(main.getConfig().getString("messages.rank-demote"));
		this.banAnnounce = CoreUtils.translate(main.getConfig().getString("messages.banAnnounce"));
		this.unbanAnnounce = CoreUtils.translate(main.getConfig().getString("messages.unbanAnnounce"));
		this.blacklistAnnounce = CoreUtils.translate(main.getConfig().getString("messages.blacklistAnnounce"));
		this.unblacklistAnnounce = CoreUtils.translate(main.getConfig().getString("messages.unblacklistAnnounce"));
		this.muteAnnounce = CoreUtils.translate(main.getConfig().getString("messages.muteAnnounce"));
		this.unmuteAnnounce = CoreUtils.translate(main.getConfig().getString("messages.unmuteAnnounce"));
		this.muteCancel = CoreUtils.translate(main.getConfig().getString("messages.muteCancel"));
		this.tryToConnect = CoreUtils.translate(main.getConfig().getString("messages.banTryConnect"));
		this.chatFormat = CoreUtils.translate(main.getConfig().getString("chat.format"));
		this.kickWhitelistProxy = CoreUtils.translate(main.getConfig().getString("bungeecord.kick-whitout-using-correct-ip"));
		this.chatCooldown = CoreUtils.translate(main.getConfig().getString("chat.cooldown.message"));
		this.chatOpened = CoreUtils.translate(main.getConfig().getString("messages.chatOpenned"));
		this.chatClosed = CoreUtils.translate(main.getConfig().getString("messages.chatClosed"));
		this.chatPriorityChange = CoreUtils.translate(main.getConfig().getString("messages.chatPriorityChanged"));
		this.frozed = CoreUtils.translate(main.getConfig().getString("messages.frozed"));
		this.frozeStatus = CoreUtils.translate(main.getConfig().getString("messages.frozeStatusNotif"));
		this.enterModMode = CoreUtils.translate(main.getConfig().getString("messages.modmode"));
		this.time = CoreUtils.translate(main.getConfig().getString("messages.time"));
		this.pmFormat = CoreUtils.translate(main.getConfig().getString("messages.private-message"));
		this.scFormat = CoreUtils.translate(main.getConfig().getString("chat.staff-format"));
		this.scSymbol = main.getConfig().getString("chat.staff-symbol");
		this.nameMCLike = CoreUtils.translate(main.getConfig().getString("messages.namemc-liked"));
		this.nameMCUnlike = CoreUtils.translate(main.getConfig().getString("messages.namemc-unliked"));
		this.nameMCLikeTag = CoreUtils.translate(main.getConfig().getString("namemc.like-tag"));
		this.clickCancel = CoreUtils.translate(main.getConfig().getString("autoclicker.cancel-hit-message"));
		this.whitelistDisabled = CoreUtils.translate(main.getConfig().getString("messages.whitelist-disabled"));
		this.whitelistEnabled = CoreUtils.translate(main.getConfig().getString("messages.whitelist-enabled"));
		this.whitelistAdd = CoreUtils.translate(main.getConfig().getString("messages.whitelist-add"));
		this.whitelistRemove = CoreUtils.translate(main.getConfig().getString("messages.whitelist-remove"));
		this.whitelistBlacklist = CoreUtils.translate(main.getConfig().getString("messages.whitelist-blacklist"));
		this.whitelistKickOnList = CoreUtils.translate(main.getConfig().getString("whitelist.kick-all-message"));
		this.whitelistKickRank = CoreUtils.translate(main.getConfig().getString("whitelist.kick-only-have-rank-message"));
		this.whitelistKickBlacklist = CoreUtils.translate(main.getConfig().getString("whitelist.kick-blacklist"));
        this.bungeeIps.addAll(main.getConfig().getStringList("bungeecord.whitelist"));
		main.getConfig().getStringList("messages.punishmentInformations").forEach(str -> this.punishmentInfo.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("messages.muteHelp").forEach(str -> this.muteHelp.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("messages.banHelp").forEach(str -> this.banHelp.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("messages.rankHelp").forEach(str -> this.rankHelp.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("chat.filtered").forEach(str -> this.filteredText.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("freeze.movement-message").forEach(str -> this.movementFrozeMessage.add(CoreUtils.translate(str)));
		main.getConfig().getStringList("messages.randomTeleport").forEach(str -> this.randomTeleport.add(CoreUtils.translate(str)));
	}

}

package akyto.core.handler.loader;

import akyto.core.Core;
import lombok.Getter;

@Getter
public class Permission {
	
	private final String rankAdmin;
	private final String rankUp;
	private final String rankDown;
	private final String addBan;
	private final String removeBan;
	private final String staffAnnounce;
	private final String viewCps;
	private final String bypassCooldownChat;
	private final String bypassFilterChat;
	private final String clearChat;
	private final String manageChat;
	private final String bypassChatClosed;
	private final String freeze;
	private final String modMode;
	private final String disguise;
	
	public Permission(final Core main) {
		this.rankAdmin = main.getConfig().getString("permissions.rank-admin");
		this.rankUp = main.getConfig().getString("permissions.rank-up");
		this.addBan = main.getConfig().getString("permissions.addBan");
		this.removeBan = main.getConfig().getString("permissions.removeBan");
		this.rankDown = main.getConfig().getString("permissions.rank-down");
		this.staffAnnounce = main.getConfig().getString("permissions.staff-announce");
		this.viewCps = main.getConfig().getString("permissions.view-cps");
		this.bypassCooldownChat = main.getConfig().getString("permissions.chat-cooldown-bypass");
		this.bypassFilterChat = main.getConfig().getString("permissions.chat-filter-bypass");
		this.clearChat = main.getConfig().getString("permissions.chat-clear");
		this.manageChat = main.getConfig().getString("permissions.chat-manage");
		this.bypassChatClosed = main.getConfig().getString("permissions.chat-closed-bypass");
		this.freeze = main.getConfig().getString("permissions.freeze");
		this.modMode = main.getConfig().getString("permissions.modmode");
		this.disguise = main.getConfig().getString("permissions.disguise");
	}

}

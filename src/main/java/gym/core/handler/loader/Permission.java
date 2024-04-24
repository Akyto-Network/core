package gym.core.handler.loader;

import gym.core.Core;
import lombok.Getter;

@Getter
public class Permission {
	
	private String rankAdmin;
	private String rankUp;
	private String rankDown;
	private String addBan;
	private String removeBan;
	private String staffAnnounce;
	private String viewCps;
	private String bypassCooldownChat;
	private String bypassFilterChat;
	private String clearChat;
	private String manageChat;
	private String bypassChatClosed;
	private String freeze;
	
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
	}

}

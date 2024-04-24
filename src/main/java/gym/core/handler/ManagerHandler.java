package gym.core.handler;

import gym.core.Core;
import gym.core.handler.manager.InventoryManager;
import gym.core.handler.manager.ProfileManager;
import gym.core.handler.manager.PunishmentManager;
import gym.core.handler.manager.RankManager;
import gym.core.handler.manager.ServerManager;
import lombok.Getter;

@Getter
public class ManagerHandler {
	
	private ProfileManager profileManager;
	private RankManager rankManager;
	private PunishmentManager punishmentManager;
	private ServerManager serverManager;
	private InventoryManager inventoryManager;
	
	public ManagerHandler(final Core main) {
		this.rankManager = new RankManager(main);
		this.profileManager = new ProfileManager(main);
		this.punishmentManager = new PunishmentManager(main);
		this.serverManager = new ServerManager(main.getConfig().getString("chat.state"), main.getConfig().getString("chat.cooldown.priority"));
		this.inventoryManager = new InventoryManager(main);
	}

}

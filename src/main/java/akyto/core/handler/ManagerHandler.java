package akyto.core.handler;

import akyto.core.Core;
import akyto.core.handler.manager.*;
import lombok.Getter;

@Getter
public class ManagerHandler {
	
	private final ProfileManager profileManager;
	private final RankManager rankManager;
	private final TagManager tagManager;
	private final PunishmentManager punishmentManager;
	private final ServerManager serverManager;
	private final InventoryManager inventoryManager;
	private final GiveawayManager giveawayManager;
	
	public ManagerHandler(final Core main) {
		this.rankManager = new RankManager(main);
		this.tagManager = new TagManager();
		this.profileManager = new ProfileManager(main);
		this.punishmentManager = new PunishmentManager(main);
		this.serverManager = new ServerManager(main.getConfig().getString("chat.state"), main.getConfig().getString("chat.cooldown.priority"));
		this.inventoryManager = new InventoryManager();
		this.giveawayManager = new GiveawayManager();
	}

}

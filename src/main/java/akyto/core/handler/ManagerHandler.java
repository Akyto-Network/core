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

		final String state = main.getConfig().getString("chat.state");
		final String cooldown = main.getConfig().getString("chat.cooldown.priority");
		this.serverManager = new ServerManager(state, cooldown);

		this.inventoryManager = new InventoryManager();
		this.giveawayManager = new GiveawayManager();
	}

}

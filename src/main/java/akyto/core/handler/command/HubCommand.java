package akyto.core.handler.command;

import org.bukkit.entity.Player;

import akyto.core.Core;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

public class HubCommand {

	@Command(name = "hub", aliases= {"lobby"}, inGameOnly = true)
	public void hubCommand(final CommandArgs arg){
		final Player sender = arg.getPlayer();
		CoreUtils.sendServer(sender, "Connect", Core.API.getLoaderHandler().getSettings().getHubInstance());
    }

}

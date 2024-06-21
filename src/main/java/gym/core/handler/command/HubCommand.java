package gym.core.handler.command;

import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class HubCommand {

	@Command(name = "hub", aliases= {"lobby"}, inGameOnly = true)
	public void hubCommand(final CommandArgs arg){
		final Player sender = arg.getPlayer();
		Utils.sendServer(sender, "Connect", Core.API.getLoaderHandler().getSettings().getHubInstance());
    }

}

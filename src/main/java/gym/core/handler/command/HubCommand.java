package gym.core.handler.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import kezukdev.akyto.profile.ProfileState;

public class HubCommand {

	@Command(name = "hub", aliases= {"lobby"}, inGameOnly = true)
	public void hubCommand(final CommandArgs arg){
		final Player sender = arg.getPlayer();

		if (kezukdev.akyto.utils.Utils.getProfiles(sender.getUniqueId()).isInState(ProfileState.FIGHT)) {
			sender.sendMessage(ChatColor.RED + "Sorry but you cannot do that in fight!");
			return;
		}

		Utils.sendServer(sender, "Connect", Core.API.getLoaderHandler().getSettings().getHubInstance());
    }

}

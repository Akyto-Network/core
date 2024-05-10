package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import kezukdev.akyto.profile.ProfileState;

public class HubCommand {

	@Command(name = "hub", aliases= {"lobby"}, inGameOnly = true)
	public void hubCommand(final CommandArgs arg){
		final CommandSender sender = arg.getSender();
		if (!(sender instanceof Player)) return;
		if (kezukdev.akyto.utils.Utils.getProfiles(kezukdev.akyto.utils.Utils.getUUID(sender.getName())).getProfileState().equals(ProfileState.FIGHT)) {
			sender.sendMessage(ChatColor.RED + "Sorry but you cannot do that in fight!");
			return;
		}
		Utils.sendServer(Bukkit.getPlayer(sender.getName()), "Connect", Core.API.getLoaderHandler().getSettings().getHubInstance());
		return;
	}

}

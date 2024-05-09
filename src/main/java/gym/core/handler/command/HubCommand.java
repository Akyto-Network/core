package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.utils.Utils;
import kezukdev.akyto.profile.ProfileState;

public class HubCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		if (kezukdev.akyto.utils.Utils.getProfiles(kezukdev.akyto.utils.Utils.getUUID(sender.getName())).getProfileState().equals(ProfileState.FIGHT)) {
			sender.sendMessage(ChatColor.RED + "Sorry but you cannot do that in fight!");
			return false;
		}
		Utils.sendServer(Bukkit.getPlayer(sender.getName()), "Connect", Core.API.getLoaderHandler().getSettings().getHubInstance());
		return false;
	}

}

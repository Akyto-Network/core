package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import kezukdev.akyto.profile.Profile;
import kezukdev.akyto.profile.ProfileState;
import kezukdev.akyto.utils.Utils;

public class ModCommand {
	
	private final Core main = Core.API;

	@Command(name = "mod", aliases= {"staff"}, inGameOnly = true)
	public void modCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		if (!(sender instanceof Player)) return;
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getModMode())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		final Profile profile = this.main.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(sender.getName()).getUniqueId());
		if (profile.isInState(ProfileState.EDITOR, ProfileState.QUEUE, ProfileState.SPECTATE, ProfileState.FIGHT)) {
			sender.sendMessage(ChatColor.RED + "You cannot do this right now!");
			return;
		}
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getEnterModMode().replace("%type%", profile.isInState(ProfileState.MOD) ? "Quit" : "Enter").replace("%subType%", profile.isInState(ProfileState.MOD) ? "enter" : "left"));
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (profile.isInState(ProfileState.MOD)) player.showPlayer(Bukkit.getPlayer(sender.getName()));
			if (profile.isInState(ProfileState.FREE)) player.hidePlayer(Bukkit.getPlayer(sender.getName()));
		});
		if (profile.isInState(ProfileState.FREE)) {
			Bukkit.getPlayer(sender.getName()).setAllowFlight(true);
		}
		if (profile.isInState(ProfileState.MOD)) {
			Utils.sendToSpawn(Bukkit.getPlayer(sender.getName()).getUniqueId(), true);
		}
		profile.setProfileState(profile.isInState(ProfileState.MOD) ? ProfileState.FREE : ProfileState.MOD);
		this.main.getPracticeAPI().getManagerHandler().getItemManager().giveItems(Bukkit.getPlayer(sender.getName()).getUniqueId(), false);
		return;
	}

}

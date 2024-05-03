package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import kezukdev.akyto.profile.Profile;
import kezukdev.akyto.profile.ProfileState;

public class ModCommand implements CommandExecutor {
	
	private Core main;
	
	public ModCommand(final Core main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getModMode())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return false;
		}
		ProfileState profileState = this.main.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(sender.getName()).getUniqueId()).getProfileState();
		if (profileState.equals(ProfileState.EDITOR) || profileState.equals(ProfileState.QUEUE) || profileState.equals(ProfileState.SPECTATE) || profileState.equals(ProfileState.FIGHT)) {
			sender.sendMessage(ChatColor.RED + "You cannot do this right now!");
			return false;
		}
		final Profile profile = this.main.getPracticeAPI().getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(sender.getName()).getUniqueId());
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getEnterModMode().replace("%type%", profileState.equals(ProfileState.MOD) ? "Quit" : "Enter").replace("%subType%", profileState.equals(ProfileState.MOD) ? "enter" : "left"));
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (profileState.equals(ProfileState.MOD)) player.showPlayer(Bukkit.getPlayer(sender.getName()));
			if (profileState.equals(ProfileState.FREE)) player.hidePlayer(Bukkit.getPlayer(sender.getName()));
		});
		if (profileState.equals(ProfileState.FREE)) {
			Bukkit.getPlayer(sender.getName()).setAllowFlight(true);
		}
		if (profileState.equals(ProfileState.MOD)) {
			this.main.getPracticeAPI().getUtils().sendToSpawn(Bukkit.getPlayer(sender.getName()).getUniqueId(), true);
		}
		profile.setProfileState(profileState.equals(ProfileState.MOD) ? ProfileState.FREE : ProfileState.MOD);
		this.main.getPracticeAPI().getManagerHandler().getItemManager().giveItems(Bukkit.getPlayer(sender.getName()).getUniqueId(), false);
		return false;
	}

}

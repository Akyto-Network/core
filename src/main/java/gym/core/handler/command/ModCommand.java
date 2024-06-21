package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.profile.Profile;
import gym.core.profile.ProfileStatus;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import gym.core.utils.item.ItemUtils;

public class ModCommand {
	
	private final Core main = Core.API;

	@Command(name = "mod", aliases= {"staff"}, inGameOnly = true)
	public void modCommand(final CommandArgs arg) {
		final Player sender = arg.getPlayer();
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getModMode())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(sender.getUniqueId());
		if (profile.isInState(ProfileStatus.UNABLE)) {
			sender.sendMessage(ChatColor.RED + "You cannot do this right now!");
			return;
		}
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getEnterModMode().replace("%type%", profile.isInState(ProfileStatus.MOD) ? "Quit" : "Enter").replace("%subType%", profile.isInState(ProfileStatus.MOD) ? "enter" : "left"));
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (profile.isInState(ProfileStatus.MOD)) player.showPlayer(sender);
			if (profile.isInState(ProfileStatus.FREE)) player.hidePlayer(sender);
		});
		profile.setStatus(profile.isInState(ProfileStatus.MOD) ? ProfileStatus.FREE : ProfileStatus.MOD);
		if (profile.isInState(ProfileStatus.MOD)) {
			sender.setAllowFlight(true);
			profile.setPreviousInventory(sender.getInventory());
			sender.getInventory().setItem(0, ItemUtils.createItems(Material.PAPER, ChatColor.YELLOW + "View CPS " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(1, ItemUtils.createItems(Material.PACKED_ICE, ChatColor.YELLOW + "Freeze " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(4, ItemUtils.createItems(Material.NETHER_STAR, ChatColor.YELLOW + "Random Teleport " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(7, ItemUtils.createItems(Material.SKULL_ITEM, ChatColor.YELLOW + "View Stats " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(8, ItemUtils.createItems(Material.REDSTONE_TORCH_ON, ChatColor.RED + "Leave Staff-Mode."));
		}
		if (profile.isInState(ProfileStatus.FREE)) {
			sender.teleport(sender.getWorld().getSpawnLocation());
			sender.getInventory().clear();
			sender.getInventory().setContents(profile.getPreviousInventory().getContents());
			sender.getInventory().setArmorContents(profile.getPreviousInventory().getArmorContents());
		}
		sender.updateInventory();
    }

}

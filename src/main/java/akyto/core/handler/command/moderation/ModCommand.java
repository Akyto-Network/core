package akyto.core.handler.command.moderation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.profile.ProfileState;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.item.ItemUtils;

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
		if (profile.isInState(ProfileState.QUEUE) || profile.isInState(ProfileState.FIGHT) || profile.isInState(ProfileState.EDITOR) || profile.isInState(ProfileState.SPECTATE)) {
			sender.sendMessage(ChatColor.RED + "You cannot do this right now!");
			return;
		}
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getEnterModMode().replace("%type%", profile.isInState(ProfileState.MOD) ? "Quit" : "Enter").replace("%subType%", profile.isInState(ProfileState.MOD) ? "enter" : "left"));
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (profile.isInState(ProfileState.MOD)) player.showPlayer(sender);
			if (profile.isInState(ProfileState.FREE)) player.hidePlayer(sender);
		});
		profile.setProfileState(profile.isInState(ProfileState.MOD) ? ProfileState.FREE : ProfileState.MOD);
		if (profile.isInState(ProfileState.MOD)) {
			sender.setAllowFlight(true);
			profile.setPreviousContents(sender.getInventory().getContents());
			profile.setPreviousArmor(sender.getInventory().getArmorContents());
			sender.getInventory().clear();
			sender.getInventory().setItem(0, ItemUtils.createItems(Material.PAPER, ChatColor.YELLOW + "View CPS " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(1, ItemUtils.createItems(Material.PACKED_ICE, ChatColor.YELLOW + "Freeze " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(4, ItemUtils.createItems(Material.NETHER_STAR, ChatColor.YELLOW + "Random Teleport " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(7, ItemUtils.createItems(Material.SKULL_ITEM, ChatColor.YELLOW + "View Stats " + ChatColor.GRAY + "(Right-Click)"));
			sender.getInventory().setItem(8, ItemUtils.createItems(Material.REDSTONE_TORCH_ON, ChatColor.RED + "Leave Staff-Mode."));
		}
		if (profile.isInState(ProfileState.FREE)) {
			sender.setFlying(false);
			sender.setAllowFlight(false);
			sender.teleport(sender.getWorld().getSpawnLocation());
			sender.getInventory().clear();
			sender.getInventory().setContents(profile.getPreviousContents());
			sender.getInventory().setArmorContents(profile.getPreviousArmor());
		}
		sender.updateInventory();
    }

}

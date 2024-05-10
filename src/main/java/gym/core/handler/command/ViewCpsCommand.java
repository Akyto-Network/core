package gym.core.handler.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import gym.core.Core;
import gym.core.profile.Profile;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class ViewCpsCommand {
	
	private Core main = Core.API;
    public static Map<Player, Profile> verifiers = new HashMap<>();
    
	@Command(name = "viewcps", aliases= {"verif", "ss"}, inGameOnly = true)
	public void viewCpsCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (!(sender instanceof Player)) return;
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getViewCps())){
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		if (args.length != 1){
			sender.sendMessage(ChatColor.RED + "/viewcps <player>");
			return;
    	}
	    if (Bukkit.getPlayer(args[0]) == null){
	    	sender.sendMessage(ChatColor.RED + "Target isn't connected.");
	    	return;
	    }
	    Inventory i = Bukkit.createInventory(null, InventoryType.DISPENSER, ChatColor.GRAY + "Viewing CPS » " + args[0]);
	    verifiers.put((Player) sender, this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[0]).getUniqueId()));
	    ((Player)sender).openInventory(i);
		return;
	}
}

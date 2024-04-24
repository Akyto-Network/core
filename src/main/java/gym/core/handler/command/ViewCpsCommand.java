package gym.core.handler.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import gym.core.Core;
import gym.core.profile.Profile;

public class ViewCpsCommand implements CommandExecutor {
	
	private Core main;
    public static Map<Player, Profile> verifiers = new HashMap<Player, Profile>();
    
    public ViewCpsCommand(final Core main){
    	this.main = main;
    }
    
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) return false;
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getViewCps())){
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return false;
		}
		if (args.length != 1){
			sender.sendMessage(ChatColor.RED + "/viewcps <player>");
			return false;
    	}
	    if (Bukkit.getPlayer(args[0]) == null){
	    	sender.sendMessage(ChatColor.RED + "Target isn't connected.");
	    	return false;
	    }
	    Inventory i = Bukkit.createInventory(null, InventoryType.DISPENSER, ChatColor.GRAY + "Viewing CPS » " + args[0]);
	    verifiers.put((Player) sender, this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[0]).getUniqueId()));
	    ((Player)sender).openInventory(i);
		return true;
	}
}

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
	
	private final Core main = Core.API;
    public static Map<Player, Profile> verifiers = new HashMap<>();
    
	@Command(name = "viewcps", aliases= {"verif", "ss"}, inGameOnly = true)
	public void viewCpsCommand(final CommandArgs arg) {
		final Player sender = arg.getPlayer();
		final String[] args = arg.getArgs();

		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getViewCps())){
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}

		if (args.length != 1){
			sender.sendMessage(ChatColor.RED + "/viewcps <player>");
			return;
    	}

		final Player target = Bukkit.getPlayer(args[0]);

	    if (target == null){
	    	sender.sendMessage(ChatColor.RED + args[0] + " isn't connected.");
	    	return;
	    }

	    Inventory i = Bukkit.createInventory(null, InventoryType.DISPENSER, ChatColor.GRAY + "Viewing CPS » " + args[0]);
	    verifiers.put(sender, this.main.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId()));
	    sender.openInventory(i);
    }
}

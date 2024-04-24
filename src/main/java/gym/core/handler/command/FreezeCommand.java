package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.runnable.FrozenRunnable;
import net.md_5.bungee.api.ChatColor;

public class FreezeCommand implements CommandExecutor {
    
    private final Core main;
    
    public FreezeCommand(final Core main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        
        if (!player.hasPermission(this.main.getLoaderHandler().getPermission().getFreeze())) {
            player.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return false;
        }
        
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/" + cmd.getName() + " <player>");
            return false;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + args[0] + " isn't connected!");
            return false;
        }
        
        boolean isFrozen = !this.main.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId()).isFrozen();
        this.main.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId()).setFrozen(isFrozen);
        
        if (isFrozen) {
            if (this.main.getLoaderHandler().getSettings().isFreezeInventory()) {
                target.openInventory(this.main.getManagerHandler().getInventoryManager().getFrozeInventory());    
            }
            target.sendMessage(this.main.getLoaderHandler().getMessage().getFrozed().replace("%frozer%", player.getName()));
            this.main.getManagerHandler().getProfileManager().getFrozed().add(target.getUniqueId());
            new FrozenRunnable(main, target.getUniqueId()).runTaskTimer(main, 2L, 2L);
        } else {
            if (this.main.getLoaderHandler().getSettings().isFreezeInventory()) {
                target.closeInventory();    
                this.main.getManagerHandler().getProfileManager().getFrozed().remove(target.getUniqueId());
            }
        }
        
        player.sendMessage(this.main.getLoaderHandler().getMessage().getFrozeStatus().replace("%frozed%", target.getName()).replace("%frozeStatus%", isFrozen ? "frozen" : "unfrozen"));
        
        return true;
    }
}

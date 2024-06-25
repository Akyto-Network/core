package akyto.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import akyto.core.Core;
import akyto.core.runnable.FrozenRunnable;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;

public class FreezeCommand {
    
    private final Core main = Core.API;

    @Command(name = "freeze", aliases= {"frozen", "froze", "frozed", "freezed"}, inGameOnly = true)
    public void freezeCommand(final CommandArgs arg) {
    	final String[] args = arg.getArgs();
        Player player = arg.getPlayer();
        
        if (!player.hasPermission(this.main.getLoaderHandler().getPermission().getFreeze())) {
            player.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return;
        }
        
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/" + arg.getCommand() + " <player>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + args[0] + " isn't connected!");
            return;
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
    }
}

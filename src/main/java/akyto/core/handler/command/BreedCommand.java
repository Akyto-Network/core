package akyto.core.handler.command;

import akyto.core.Core;
import akyto.core.handler.manager.ProfileManager;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BreedCommand {

    @Command(name = "breedpass", aliases= {"bp"}, inGameOnly = false)
    public void bypassCpsCapCommand(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        if (sender.hasPermission(Core.API.getLoaderHandler().getPermission().getBypassCpsCap())) {
            sender.sendMessage(ChatColor.RED + Core.API.getLoaderHandler().getMessage().getNoPermission());
            return;
        }
        if (arg.getArgs().length != 0) {
            arg.getSender().sendMessage(ChatColor.RED + "/breedpass <player>");
            return;
        }
        String target = arg.getArgs(0);
        final ProfileManager profileManager = Core.API.getManagerHandler().getProfileManager();
        if (profileManager.getRealNameInDisguised().containsKey(target)){
            target = profileManager.getRealNameInDisguised().get(target);
        }
        if (Core.API.getBypassCpsCap().contains(target)) {
            arg.getSender().sendMessage(ChatColor.RED + target + " have already the bypass!");
            return;
        }
        Core.API.getBypassCpsCap().add(target);
        if (Bukkit.getPlayer(target) != null) Bukkit.getPlayer(target).sendMessage(ChatColor.GREEN + "You've been get a bypass for the cps-cap!");
        arg.getSender().sendMessage(ChatColor.GREEN + target + " successfully added to the bypass list!");
    }
}

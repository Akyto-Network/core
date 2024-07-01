package akyto.core.handler.command;

import akyto.core.Core;
import akyto.core.disguise.DisguiseEntry;
import akyto.core.handler.ManagerHandler;
import akyto.core.handler.manager.ServerManager;
import akyto.core.profile.Profile;
import akyto.core.profile.ProfileState;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.spigot.math.FastRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class DisguiseCommand {

    @Command(name = "disguise", aliases= {"nick"}, inGameOnly = true)
    public void giveaway(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        final String[] args = arg.getArgs();
        final ManagerHandler managerHandler = Core.API.getManagerHandler();
        final Profile profile = managerHandler.getProfileManager().getProfiles().get(sender.getUniqueId());
        if (!sender.hasPermission(Core.API.getLoaderHandler().getPermission().getDisguise())) {
            sender.sendMessage(CoreUtils.translate(Core.API.getLoaderHandler().getMessage().getNoPermission()));
        }
        if (profile.isDisguised()) {
            Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().remove(Core.API.getManagerHandler().getProfileManager().getDisguised().get(sender.getUniqueId()).getName());
            sender.clearFakeNamesAndSkins();
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.hidePlayer(sender);
            });
            Core.API.getManagerHandler().getProfileManager().getDisguised().remove(sender.getUniqueId());
            profile.setDisguised(false);
            final String name = sender.getName().substring(0, Math.min(sender.getName().length(), 14));
            sender.setPlayerListName(CoreUtils.translate(Core.API.getManagerHandler().getProfileManager().getRank(sender.getUniqueId()).getColor()) + name);
            sender.sendMessage(ChatColor.GRAY + "You have just returned to normal!");
            Bukkit.getScheduler().runTaskLater(Core.API, () -> {
                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.showPlayer(sender);
                });
            },2L);
            sender.setDisplayName(sender.getName());
            return;
        }
        if (!profile.isInState(ProfileState.FREE)) {
            sender.sendMessage(ChatColor.RED + "");
        }
        Random random = new FastRandom();
        final UUID uuid = sender.getUniqueId();
        final ServerManager serverManager = managerHandler.getServerManager();
        Set<Map.Entry<String, DisguiseEntry>> entries = serverManager.getDisguise().entrySet();
        Map.Entry<String, DisguiseEntry>[] entriesArray = entries.toArray(new Map.Entry[0]);
        Map.Entry<String, DisguiseEntry> randomEntry = entriesArray[random.nextInt(entriesArray.length)];
        DisguiseEntry disguiseEntry = randomEntry.getValue();
        if (!Core.API.getManagerHandler().getProfileManager().getDisguised().isEmpty()) {
            boolean nameTaken = true;
            while (nameTaken) {
                for (Map.Entry<UUID, DisguiseEntry> entry : Core.API.getManagerHandler().getProfileManager().getDisguised().entrySet()) {
                    if (entry.getValue().equals(disguiseEntry)) {
                        randomEntry = entriesArray[random.nextInt(entriesArray.length)];
                        disguiseEntry = randomEntry.getValue();
                        nameTaken = true;
                        break;
                    }
                }
                nameTaken = false;
            }
        }
        DisguiseEntry finalDisguiseEntry = disguiseEntry;
        Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().put(finalDisguiseEntry.getName(), sender.getName());
        Bukkit.getOnlinePlayers().forEach(players -> {
            CoreUtils.disguise(players, sender, finalDisguiseEntry);
            players.hidePlayer(sender);
        });
        Bukkit.getScheduler().runTaskLater(Core.API, ()-> {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.showPlayer(sender);
            });
        },2L);
        Core.API.getManagerHandler().getProfileManager().getDisguised().put(sender.getUniqueId(), finalDisguiseEntry);
        sender.setPlayerListName(ChatColor.GREEN + finalDisguiseEntry.getName());
        sender.setDisplayName(finalDisguiseEntry.getName());
        profile.setDisguised(true);
        sender.teleport(sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "You've been disguised now and you'r name is: " + ChatColor.RED + finalDisguiseEntry.getName());
    }
}

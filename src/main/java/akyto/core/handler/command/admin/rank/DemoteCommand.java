package akyto.core.handler.command.admin.rank;

import akyto.core.Core;
import akyto.core.handler.loader.Message;
import akyto.core.handler.loader.Permission;
import akyto.core.handler.manager.ProfileManager;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.database.DatabaseType;
import co.aikar.idb.DB;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DemoteCommand {

    private final Core main = Core.API;

    @Command(name = "demote", aliases= {"rankdown"}, inGameOnly = false)
    public void demoteCommand(final CommandArgs arg) {

        final CommandSender sender = arg.getSender();
        final String[] args = arg.getArgs();

        final ProfileManager profileManager = this.main.getManagerHandler().getProfileManager();
        final Message message = this.main.getLoaderHandler().getMessage();
        final Permission perm = this.main.getLoaderHandler().getPermission();

        if (!sender.hasPermission(perm.getRankAdmin()) || !sender.hasPermission(perm.getRankUp()) || !sender.hasPermission(perm.getRankDown())) {
            sender.sendMessage(message.getNoPermission());
            return;
        }

        // HELP
        if (args.length == 0) {
            message.getRankHelp().forEach(sender::sendMessage);
            return;
        }

        if (args.length == 1 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankDown())) {

            final Player target = Bukkit.getPlayer(args[0]);
            final boolean online = target != null;
            final UUID uuid = online ? target.getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId();

            if (this.main.getDatabaseType().equals(DatabaseType.FLAT_FILES) && !profileManager.getProfiles().containsKey(uuid)) {
                sender.sendMessage(message.getNeverPlayed()
                        .replace("%target%", args[0])
                );
                return;
            }

            if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
                try {
                    if (!online && this.main.getMySQL().existPlayerManagerAsync(uuid).get()) {
                        DB.executeUpdateAsync("UPDATE playersdata SET rank=? WHERE name=?", "default" , args[0]);
                    }
                } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
            }

            if (profileManager.getProfiles().containsKey(uuid)) {
                profileManager.getProfile(uuid).setRank("default");
            }

            if (online) {
                final String nameShrink = target.getName().substring(0, Math.min(target.getDisplayName().length(), 14));
                target.setPlayerListName(
                        CoreUtils.translate(profileManager.getRank(uuid).getColor() + nameShrink)
                );

            }

            if (this.main.getLoaderHandler().getSettings().isRankDemoteBroad()) {
                Bukkit.broadcastMessage(message.getRankDown()
                        .replace("%target%", args[0])
                );
            }
        }
    }
}

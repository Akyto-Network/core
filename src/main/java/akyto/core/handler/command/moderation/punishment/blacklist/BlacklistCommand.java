package akyto.core.handler.command.moderation.punishment.blacklist;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.punishment.cache.BlacklistEntry;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BlacklistCommand {

    private final Core main = Core.API;

    @Command(name = "blacklist", aliases= {"ban-ip"})
    public void blacklistCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        final String[] args = arg.getArgs();
        if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getAddBan())) {
            sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return;
        }

        if (args.length == 0 || args.length > 2) {
            this.main.getLoaderHandler().getMessage().getBanHelp().forEach(sender::sendMessage);
            return;
        }

        if (args[0].equalsIgnoreCase("info") && args.length == 2) {
            if (!this.main.getManagerHandler().getPunishmentManager().getBlacklisted().containsKey(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "blacklist"));
                return;
            }
            final BlacklistEntry ban = this.main.getManagerHandler().getPunishmentManager().getBlacklisted().get(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            this.main.getLoaderHandler().getMessage().getPunishmentInfo().forEach(str -> sender.sendMessage(str
                    .replace("%user%", args[1])
                    .replace("%punishmentType%", "BLACKLISTED")
                    .replace("%reason%", ban.getReason())
                    .replace("%judge%", ban.getJudge())));
            return;
        }
        String reason = "Unfair Advantage";
        reason = args.length > 1 ? StringUtils.join(args, ' ', 2, args.length) : "Unfair Advantage";
        String target = args[0];
        if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])){
            target = Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(args[0]);
        }
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), null, reason, sender.getName(), PunishmentType.BLACKLIST);
        if (this.main.getLoaderHandler().getSettings().isBanBroad() && !sender.getName().equalsIgnoreCase("CONSOLE")) {
            Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getBlacklistAnnounce().replace("%banned%", target).replace("%reason%", reason).replace("%judge%", sender.getName()));
        }
    }
}

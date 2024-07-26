package akyto.core.handler.command.moderation.punishment.blacklist;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnblacklistCommand {

    private final Core main = Core.API;

    @Command(name = "unblacklist", aliases= {"unban-ip"})
    public void unblacklistCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        final String[] args = arg.getArgs();

        if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getRemoveBan())) {
            sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return;
        }

        if (args.length != 1) {
            this.main.getLoaderHandler().getMessage().getBanHelp().forEach(sender::sendMessage);
            return;
        }

        UUID target = CoreUtils.getUUID(args[0]);

        this.main.getManagerHandler().getPunishmentManager().removePunishment(target, PunishmentType.BLACKLIST);
        if (this.main.getLoaderHandler().getSettings().isBanBroad()) {
            Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getUnblacklistAnnounce().replace("%banned%", args[0]).replace("%judge%", sender.getName()));
        }
    }

}

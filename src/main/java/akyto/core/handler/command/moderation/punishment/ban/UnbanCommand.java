package akyto.core.handler.command.moderation.punishment.ban;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnbanCommand {

    private final Core main = Core.API;

    @Command(name = "unban", aliases= {"unpunish", "pardon"})
    public void unbanCommand(final CommandArgs arg) {
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

        this.main.getManagerHandler().getPunishmentManager().removePunishment(target, PunishmentType.BAN);
        if (this.main.getLoaderHandler().getSettings().isBanBroad()) {
            Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getUnbanAnnounce().replace("%banned%", args[0]).replace("%judge%", sender.getName()));
        }
    }
}

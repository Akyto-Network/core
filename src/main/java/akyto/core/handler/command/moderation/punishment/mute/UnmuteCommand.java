package akyto.core.handler.command.moderation.punishment.mute;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class UnmuteCommand {

    private final Core main = Core.API;

    @Command(name = "unmute")
    public void unMuteCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        final String[] args = arg.getArgs();
        if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getRemoveBan())) {
            sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return;
        }
        if (args.length != 1) {
            this.main.getLoaderHandler().getMessage().getMuteHelp().forEach(sender::sendMessage);
            return;
        }
        this.main.getManagerHandler().getPunishmentManager().removePunishment(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), PunishmentType.MUTE);
        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
            Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getUnmuteAnnounce().replace("%muted%", args[0]).replace("%judge%", sender.getName()));
        }
    }
}

package akyto.core.handler.command.moderation.punishment.mute;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.format.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MuteCommand {

    private final Core main = Core.API;

    @Command(name = "mute")
    public void muteCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        final String[] args = arg.getArgs();
        if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getAddBan())) {
            sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
            return;
        }
        if (args.length == 0) {
            this.main.getLoaderHandler().getMessage().getMuteHelp().forEach(sender::sendMessage);
            return;
        }
        if (args.length == 1) {
            this.main.getLoaderHandler().getMessage().getMuteHelp().forEach(sender::sendMessage);
            return;
        }
        if (args[0].equalsIgnoreCase("info") && args.length == 2) {
            if (!this.main.getManagerHandler().getPunishmentManager().getMuted().containsKey(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "mute"));
                return;
            }
            final MuteEntry ban = this.main.getManagerHandler().getPunishmentManager().getMuted().get(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            this.main.getLoaderHandler().getMessage().getPunishmentInfo().forEach(str -> sender.sendMessage(str.replace("%user%", args[1]).replace("%punishmentType%", "MUTE").replace("%expires%", ban.getExpiresOn()).replace("%reason%", ban.getReason()).replace("%judge%", ban.getJudge())));
            return;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        new TimeUtils(args[1], calendar);
        String reason = args.length > 2 ? StringUtils.join(args, ' ', 2, args.length) : "Flood";
        String target = args[0];
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName(), PunishmentType.MUTE);
        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
            Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getMuteAnnounce().replace("%muted%", target).replace("%reason%", reason).replace("%judge%", sender.getName()));
        }
    }
}

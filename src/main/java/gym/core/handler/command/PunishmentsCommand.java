package gym.core.handler.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import gym.core.Core;
import gym.core.punishment.PunishmentType;
import gym.core.punishment.cache.BanEntry;
import gym.core.punishment.cache.MuteEntry;
import gym.core.utils.CoreUtils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import gym.core.utils.format.TimeUtils;

public class PunishmentsCommand {
	
	private final Core main = Core.API;
	
	// BAN CATEGORY //
	
	@Command(name = "ban", aliases= {"punish"})
	public void banCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getAddBan())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}

		if (args.length <= 1) {
			this.main.getLoaderHandler().getMessage().getBanHelp().forEach(sender::sendMessage);
			return;
		}

		if (args[0].equalsIgnoreCase("info") && args.length == 2) {
			if (!this.main.getManagerHandler().getPunishmentManager().getBanned().containsKey(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "ban"));
				return;
			}
			final BanEntry ban = this.main.getManagerHandler().getPunishmentManager().getBanned().get(Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId());
			this.main.getLoaderHandler().getMessage().getPunishmentInfo().forEach(str -> sender.sendMessage(str.replace("%user%", args[1]).replace("%punishmentType%", "BANNED").replace("%expires%", ban.getExpiresOn()).replace("%reason%", ban.getReason()).replace("%judge%", ban.getJudge())));
			return;
		}
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	String reason = "Unfair Advantage";
		new TimeUtils(args[1], calendar);
        reason = args.length > 2 ? StringUtils.join(args, ' ', 2, args.length) : "Unfair Advantage";  
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName(), PunishmentType.BAN);
        if (this.main.getLoaderHandler().getSettings().isBanBroad() && !sender.getName().equalsIgnoreCase("CONSOLE")) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getBanAnnounce().replace("%banned%", args[0]).replace("%reason%", reason).replace("%judge%", sender.getName()));
        }
    }
	
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
	
	// MUTE CATEGORY //
	
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
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName(), PunishmentType.MUTE);
        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getMuteAnnounce().replace("%muted%", args[0]).replace("%reason%", reason).replace("%judge%", sender.getName()));
        }
    }

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

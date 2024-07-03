package akyto.core.handler.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import akyto.core.punishment.cache.BlacklistEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import akyto.core.Core;
import akyto.core.punishment.PunishmentType;
import akyto.core.punishment.cache.BanEntry;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.format.TimeUtils;
import org.bukkit.entity.Player;

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
		String target = args[0];
		if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])){
			target = Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(args[0]);
		}
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName(), PunishmentType.BAN);
        if (this.main.getLoaderHandler().getSettings().isBanBroad() && !sender.getName().equalsIgnoreCase("CONSOLE")) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getBanAnnounce().replace("%banned%", target).replace("%reason%", reason).replace("%judge%", sender.getName()));
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

	// BLACKLIST CATEGORY //

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
		String target = args[0];
		if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])){
			target = Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(args[0]);
		}
        this.main.getManagerHandler().getPunishmentManager().addPunishment(Bukkit.getPlayer(target) != null ? Bukkit.getPlayer(target).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName(), PunishmentType.MUTE);
        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getMuteAnnounce().replace("%muted%", target).replace("%reason%", reason).replace("%judge%", sender.getName()));
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

package gym.core.handler.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import gym.core.Core;
import gym.core.punishment.BanEntry;
import gym.core.utils.TimeUtils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class BanCommand {
	
	private final Core main = Core.API;
	
	@Command(name = "ban", aliases= {"punish"}, inGameOnly = false)
	public void banCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getAddBan())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		if (args.length == 1) {
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
        this.main.getManagerHandler().getPunishmentManager().addBan(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName());
        if (this.main.getLoaderHandler().getSettings().isBanBroad() && !sender.getName().equalsIgnoreCase("CONSOLE")) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getBanAnnounce().replace("%banned%", args[0]).replace("%reason%", reason).replace("%judge%", sender.getName()));
        }
		return;
	}
	
	@Command(name = "unban", aliases= {"unpunish"}, inGameOnly = true)
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
        this.main.getManagerHandler().getPunishmentManager().removeBan(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
        if (this.main.getLoaderHandler().getSettings().isBanBroad()) {
        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getUnbanAnnounce().replace("%banned%", args[0]).replace("%judge%", sender.getName()));
        }
		return;
	}
}

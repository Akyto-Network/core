package gym.core.handler.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import gym.core.Core;
import gym.core.punishment.MuteEntry;
import gym.core.utils.TimeUtils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class MuteCommand {
	
	private Core main = Core.API;

	@Command(name = "mute", aliases = {"unmute"}, inGameOnly = false)
	public void muteCommand(final CommandArgs arg){
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		final org.bukkit.command.Command cmd = arg.getCommand();
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getAddBan())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		if (args.length == 0) {
			this.main.getLoaderHandler().getMessage().getMuteHelp().forEach(sender::sendMessage);
			return;
		}
		if (cmd.getName().equalsIgnoreCase("mute")) {
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
	    	String reason = "Flood";
			new TimeUtils(args[1], calendar);
            reason = args.length > 2 ? StringUtils.join(args, ' ', 2, args.length) : "Flood";  
	        this.main.getManagerHandler().getPunishmentManager().addMute(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId(), sdf.format(calendar.getTime()), reason, sender.getName());
	        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
	        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getMuteAnnounce().replace("%muted%", args[0]).replace("%reason%", reason).replace("%judge%", sender.getName()));
	        }
			return;
		}
		if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getRemoveBan())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
				return;
			}
			if (args.length != 1) {
				this.main.getLoaderHandler().getMessage().getBanHelp().forEach(sender::sendMessage);
				return;
			}
	        this.main.getManagerHandler().getPunishmentManager().removeMute(Bukkit.getPlayer(args[0]) != null ? Bukkit.getPlayer(args[0]).getUniqueId() : Bukkit.getOfflinePlayer(args[0]).getUniqueId());
	        if (this.main.getLoaderHandler().getSettings().isMuteBroad()) {
	        	Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getUnmuteAnnounce().replace("%muted%", args[0]).replace("%judge%", sender.getName()));
	        }
		}
		return;
	}

}

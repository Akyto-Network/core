package gym.core.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gym.core.Core;
import gym.core.profile.Profile;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

public class MessageCommand {
	
	private Core main = Core.API;

	@Command(name = "msg", aliases= {"message", "tell", "whisper", "r", "m", "reply"}, inGameOnly = true)
	public void messageCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		final org.bukkit.command.Command cmd = arg.getCommand();
		if (!(sender instanceof Player)) return;
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/" + cmd.getName() + (cmd.getName().contains("r") ? "<message>" : "<player> <message>"));
			return;
		}
		if (cmd.getName().contains("m")) {
			if (Bukkit.getPlayer(args[0]) == null) {
				sender.sendMessage(ChatColor.RED + "Target isn't connected!");
				return;
			}
			final Profile senderProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(sender.getName()).getUniqueId());
			final Profile receiverProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[0]).getUniqueId());
			String msg = args.length > 1 ? StringUtils.join(args, ' ', 1, args.length) : "Hi, how are you today?"; 
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
					.replace("%senderColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%sender%", sender.getName())
					.replace("%receiverColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%receiver%", args[0])
					.replace("%incommingType%", "To")
					.replace("%player%", args[0])
					.replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%rank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getPrefix()) + (this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).hasSpaceBetweenColor() ? " " : ""))
					.replace("%message%", msg));
			Bukkit.getPlayer(args[0]).sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
					.replace("%senderColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%sender%", sender.getName())
					.replace("%receiverColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%receiver%", args[0])
					.replace("%incommingType%", "From")
					.replace("%player%", sender.getName())
					.replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%rank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getPrefix()) + (this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).hasSpaceBetweenColor() ? " " : ""))
					.replace("%message%", msg));
			receiverProfile.setResponsive(Bukkit.getPlayer(sender.getName()).getUniqueId());
			return;
		}
		if (cmd.getName().contains("r")) {
			final Profile senderProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(sender.getName()).getUniqueId());
			if (senderProfile.getResponsive() == null) {
				sender.sendMessage(ChatColor.RED + "You doesn't have anyone to answer.");
				return;
			}
			if (Bukkit.getPlayer(senderProfile.getResponsive()) == null) {
				sender.sendMessage(ChatColor.RED + "Target isn't connected!");
				return;
			}
			final Profile receiverProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(senderProfile.getResponsive());
			String msg = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : "Hi, how are you today?"; 
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
					.replace("%senderColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%sender%", sender.getName())
					.replace("%receiverColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%receiver%", Bukkit.getPlayer(senderProfile.getResponsive()).getName())
					.replace("%incommingType%", "To")
					.replace("%player%", Bukkit.getPlayer(senderProfile.getResponsive()).getName())
					.replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%rank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getPrefix()) + (this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).hasSpaceBetweenColor() ? " " : ""))
					.replace("%message%", msg));
			Bukkit.getPlayer(senderProfile.getResponsive()).sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
					.replace("%senderColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%sender%", sender.getName())
					.replace("%receiverColorRank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(receiverProfile.getRank()).getColor()))
					.replace("%receiver%", Bukkit.getPlayer(senderProfile.getResponsive()).getName())
					.replace("%incommingType%", "From")
					.replace("%player%", sender.getName())
					.replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getColor()))
					.replace("%rank%", Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).getPrefix()) + (this.main.getManagerHandler().getRankManager().getRanks().get(senderProfile.getRank()).hasSpaceBetweenColor() ? " " : ""))
					.replace("%message%", msg));
			receiverProfile.setResponsive(Bukkit.getPlayer(sender.getName()).getUniqueId());
			return;
		}
		return;
	}

}

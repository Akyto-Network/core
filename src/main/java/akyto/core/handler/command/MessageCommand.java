package akyto.core.handler.command;

import akyto.core.rank.RankEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

import java.util.HashMap;
import java.util.UUID;

public class MessageCommand {
	
	private final Core main = Core.API;

	@Command(name = "msg", aliases= {"message", "tell", "whisper", "r", "m", "reply"}, inGameOnly = true)
	public void messageCommand(final CommandArgs arg) {
		final Player sender = arg.getPlayer();
		final String[] args = arg.getArgs();

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/msg <player> <message>");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])) {
			target = Bukkit.getPlayer(Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(args[0]));
		}

		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " isn't connected!");
			return;
		}

		final Profile senderProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(sender.getUniqueId());
		final Profile receiverProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId());
		String msg = args.length > 1 ? StringUtils.join(args, ' ', 1, args.length) : "Hi, how are you today?";
		final HashMap<String, RankEntry> rank = this.main.getManagerHandler().getRankManager().getRanks();
		String rankSender = rank.get(senderProfile.getRank()).getPrefix();
		String rankReceiver = rank.get(receiverProfile.getRank()).getPrefix();
		String senderColor = rank.get(senderProfile.getRank()).getColor();
		String receiverColor = rank.get(receiverProfile.getRank()).getColor();
		boolean spaceReceiver = rank.get(receiverProfile.getRank()).hasSpaceBetweenColor();
		boolean spaceSender = rank.get(senderProfile.getRank()).hasSpaceBetweenColor();
		if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])){
			rankReceiver = "";
			receiverColor = ChatColor.GREEN.toString();
			spaceReceiver = false;
		}
		if (sender.hasFakeName(sender)){
			rankSender = "";
			senderColor = ChatColor.GREEN.toString();
			spaceSender = false;
		}
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
				.replace("%senderColorRank%", senderColor)
				.replace("%sender%", sender.getDisplayName())
				.replace("%receiverColorRank%", receiverColor)
				.replace("%receiver%", target.getDisplayName())
				.replace("%incommingType%", "To")
				.replace("%player%", target.getDisplayName())
				.replace("%rankColor%", receiverColor)
				.replace("%rank%", rankReceiver + (spaceReceiver ? " " : ""))
				.replace("%message%", msg));

		target.sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
				.replace("%senderColorRank%", senderColor)
				.replace("%sender%", sender.getDisplayName())
				.replace("%receiverColorRank%", receiverColor)
				.replace("%receiver%", target.getDisplayName())
				.replace("%incommingType%", "From")
				.replace("%player%", sender.getDisplayName())
				.replace("%rankColor%", senderColor)
				.replace("%rank%", rankSender + (spaceSender ? " " : ""))
				.replace("%message%", msg));

		receiverProfile.setResponsive(sender.getUniqueId());
    }
	
	@Command(name = "reply", aliases= {"r","answer"}, inGameOnly = true)
	public void replyCommand(final CommandArgs arg) {
		final Player sender = arg.getPlayer();
		final String[] args = arg.getArgs();

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/reply <message>");
			return;
		}

		final Profile senderProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(sender.getUniqueId());

		if (senderProfile.getResponsive() == null) {
			sender.sendMessage(ChatColor.RED + "You doesn't have anyone to answer.");
			return;
		}

		if (Bukkit.getPlayer(senderProfile.getResponsive()) == null) {
			sender.sendMessage(ChatColor.RED + "Target isn't connected!");
			return;
		}

		final Profile receiverProfile = this.main.getManagerHandler().getProfileManager().getProfiles().get(senderProfile.getResponsive());
		String msg = StringUtils.join(args, ' ', 0, args.length);

		Player receiver = Bukkit.getPlayer(senderProfile.getResponsive());
		final HashMap<String, RankEntry> rank = this.main.getManagerHandler().getRankManager().getRanks();
		String rankSender = rank.get(senderProfile.getRank()).getPrefix();
		String rankReceiver = rank.get(receiverProfile.getRank()).getPrefix();
		String senderColor = rank.get(senderProfile.getRank()).getColor();
		String receiverColor = rank.get(receiverProfile.getRank()).getColor();
		boolean spaceReceiver = rank.get(receiverProfile.getRank()).hasSpaceBetweenColor();
		boolean spaceSender = rank.get(senderProfile.getRank()).hasSpaceBetweenColor();
		if (receiver.hasFakeName(sender)){
			rankReceiver = "";
			receiverColor = ChatColor.GREEN.toString();
			spaceReceiver = false;
		}
		if (sender.hasFakeName(sender)){
			rankSender = "";
			senderColor = ChatColor.GREEN.toString();
			spaceSender = false;
		}
		sender.sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
				.replace("%senderColorRank%", senderColor)
				.replace("%sender%", sender.getDisplayName())
				.replace("%receiverColorRank%", receiverColor)
				.replace("%receiver%", Bukkit.getPlayer(senderProfile.getResponsive()).getDisplayName())
				.replace("%incommingType%", "To")
				.replace("%player%", Bukkit.getPlayer(senderProfile.getResponsive()).getDisplayName())
				.replace("%rankColor%", rankSender)
				.replace("%rank%", rankReceiver + (spaceReceiver ? " " : ""))
				.replace("%message%", msg));

		Bukkit.getPlayer(senderProfile.getResponsive()).sendMessage(this.main.getLoaderHandler().getMessage().getPmFormat()
				.replace("%senderColorRank%", senderColor)
				.replace("%sender%", sender.getDisplayName())
				.replace("%receiverColorRank%", receiverColor)
				.replace("%receiver%", Bukkit.getPlayer(senderProfile.getResponsive()).getDisplayName())
				.replace("%incommingType%", "From")
				.replace("%player%", sender.getDisplayName())
				.replace("%rankColor%", senderColor)
				.replace("%rank%", rankSender + (spaceSender ? " " : ""))
				.replace("%message%", msg));

		receiverProfile.setResponsive(sender.getUniqueId());
    }
}

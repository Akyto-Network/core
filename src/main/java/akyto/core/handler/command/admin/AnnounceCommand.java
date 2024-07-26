package akyto.core.handler.command.admin;

import akyto.core.Core;
import akyto.core.utils.CoreUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

public class AnnounceCommand {
	
	@Command(name = "announce", aliases= {"bc", "broadcast", "announcement"}, permission = "akyto.admin")
	public void announceCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		String announce = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : "Broadcast as made!";
		Core.API.getLoaderHandler().getMessage().getAnnouncement().forEach(str -> {
			Bukkit.broadcastMessage(str
					.replace("%announce%", CoreUtils.translate(announce))
					.replace("%author%", sender.getName())
			);
		});
		sender.sendMessage(ChatColor.GREEN + "Your announce has been sent!");
    }
}

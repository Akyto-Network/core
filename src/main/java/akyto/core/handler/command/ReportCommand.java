package akyto.core.handler.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class ReportCommand {

	@Command(name = "report", aliases= {"reports"}, inGameOnly = true)
	public boolean reportCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (!(sender instanceof Player)) return false;
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "/report <player> <reason>");
			return false;
		}
		if (args.length > 1) {
			if (Bukkit.getPlayer(args[0]).getUniqueId() == null && Bukkit.getOfflinePlayer(args[0]) == null) {
				sender.sendMessage(ChatColor.RED + "Him has never played on the server!");
				return false;
			}
			String reason = args.length > 1 ? StringUtils.join(args, ' ', 1, args.length) : "Cheating"; 
			sender.sendMessage(ChatColor.GREEN + "You'r report has been sent to the staff team :)");
			final TextComponent comp = new TextComponent(ChatColor.GRAY + "(" + ChatColor.RED + "!" + ChatColor.GRAY + ") " + ChatColor.GREEN + sender.getName() + ChatColor.GRAY + " have reported " + ChatColor.RED + args[0] + ChatColor.GRAY + " for " + ChatColor.WHITE + reason);
			comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(Bukkit.getPlayer(args[0]) != null ? ChatColor.DARK_GRAY + "Click to teleport to reported player!" : ChatColor.RED + "The reported is offline now!").create()));
			if (Bukkit.getPlayer(args[0]) != null) comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tp " + args[0]));
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (player.hasPermission("akyto.staff")) {
                    player.spigot().sendMessage(comp);
				}
			});
		}
		return false;
	}

}
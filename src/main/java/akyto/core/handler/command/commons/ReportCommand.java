package akyto.core.handler.command.commons;

import akyto.core.Core;
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

import java.util.Objects;

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
			String target = args[0];
			if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(args[0])){
				target = Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(args[0]);
			}
			if (Bukkit.getPlayer(target).getUniqueId() == null && Bukkit.getOfflinePlayer(target) == null) {
				sender.sendMessage(ChatColor.WHITE + args[0] + ChatColor.RED + " not found on akyto.");
				return false;
			}
			String reason = StringUtils.join(args, ' ', 1, args.length);
			if (reason.isBlank())
				reason = "Cheating";
			sender.sendMessage(ChatColor.GREEN + "Your report has been sent to the staff team :)");
			final TextComponent comp = new TextComponent(ChatColor.GRAY + "(" + ChatColor.RED + "!" + ChatColor.GRAY + ") " + ChatColor.GREEN + (((Player) sender).hasFakeName(sender) ? sender.getName() + ChatColor.GRAY + "[" + ChatColor.YELLOW + ChatColor.ITALIC + ((Player) sender).getFakeName(sender) + ChatColor.GRAY + "]" : sender.getName()) + ChatColor.GRAY + " have reported " + ChatColor.RED + (!args[0].equals(target) ? target + ChatColor.GRAY + "[" + ChatColor.YELLOW + ChatColor.ITALIC + args[0] + ChatColor.GRAY + "]" : target)+ ChatColor.GRAY + " for " + ChatColor.WHITE + reason);
			comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(Bukkit.getPlayer(target) != null ? ChatColor.DARK_GRAY + "Click to teleport to reported player!" : ChatColor.RED + "The reported is offline now!").create()));
			if (Bukkit.getPlayer(target) != null) comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tp " + args[0]));
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (player.hasPermission("akyto.staff")) {
                    player.spigot().sendMessage(comp);
				}
			});
		}
		return false;
	}

}

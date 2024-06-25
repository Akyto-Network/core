package akyto.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import akyto.core.Core;
import akyto.core.chat.ChatPriority;
import akyto.core.chat.ChatState;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

public class ChatCommand {

	private final Core main = Core.API;
	
	@Command(name = "chat", aliases= {"chatmanagement"})
	public void chatCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/chat <clear/on/off/priority>");
			return;
		}
		if (args[0].equalsIgnoreCase("clear") && sender.hasPermission(this.main.getLoaderHandler().getPermission().getClearChat())) {
			if (args.length == 1 || args.length == 2) {
				try {
					int lines = args.length == 2 ? Integer.parseInt(args[1]) : 100;
					for (int i = 0; i < lines; i++) {
						Bukkit.broadcastMessage(" ");
					}
					Bukkit.broadcastMessage(ChatColor.YELLOW + "The chat has been cleared by " + ChatColor.WHITE + sender.getName());
				} catch (NumberFormatException ex) {
					sender.sendMessage(ChatColor.RED + "Please provide a integer number!");
					return;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/chat clear <linesNumber>");
			}
		}
		if ((args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) && sender.hasPermission(this.main.getLoaderHandler().getPermission().getManageChat())) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("on")) {
					if (!this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
						sender.sendMessage(ChatColor.RED + "Sorry but the chat as already openned!");
						return;
					}
					this.main.getManagerHandler().getServerManager().setChatState(ChatState.GLOBAL);
					sender.sendMessage(ChatColor.WHITE + "The " + ChatColor.RED + "chat" + ChatColor.WHITE + " has been " + ChatColor.GREEN + "enabled" + ChatColor.WHITE + "!");
					Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatOpened());
					return;
				}	
				if (args[0].equalsIgnoreCase("off")) {
					if (this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
						sender.sendMessage(ChatColor.RED + "Sorry but the chat as already closed!");
						return;
					}
					this.main.getManagerHandler().getServerManager().setChatState(ChatState.CLOSED);
					sender.sendMessage(ChatColor.WHITE + "The " + ChatColor.RED + "chat" + ChatColor.WHITE + " has been " + ChatColor.RED + "closed" + ChatColor.WHITE + "!");
					Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatClosed());
					return;
				}
			}
		}
		if (args[0].equalsIgnoreCase("priority") && sender.hasPermission(this.main.getLoaderHandler().getPermission().getManageChat())) {
			if (args.length == 2) {
				try {
					ChatPriority priority = ChatPriority.valueOf(args[1].toUpperCase());
					this.main.getManagerHandler().getServerManager().setChatPriority(priority);
					Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatPriorityChange().replace("%priority%", args[1].toLowerCase()));
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + "/chat priority <HIGH/MEDIUM/LOWER/NORMAL/SPAM>");
				}
				return;
			}
			sender.sendMessage(ChatColor.RED + "/chat priority <HIGH/MEDIUM/LOWER/NORMAL/SPAM>");
        }
    }

}

package gym.core.handler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import gym.core.Core;
import gym.core.chat.ChatPriority;
import gym.core.chat.ChatState;

public class ChatCommand implements CommandExecutor {

	private final Core main;
	
	public ChatCommand(Core main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/chat <clear/on/off/priority>");
			return false;
		}
		if (args[0].equalsIgnoreCase("clear") && sender.hasPermission(this.main.getLoaderHandler().getPermission().getClearChat())) {
			if (args.length == 1 || args.length == 2) {
				if (args.length == 1) {
					for (int i = 0; i < 100; i++) {
						Bukkit.broadcastMessage(" ");
					}
				}
				if (args.length == 2) {
					if (Integer.valueOf(args[1]) == null) {
						sender.sendMessage(ChatColor.RED + "Please provide a integer number!");
						return false;
					}
					for (int i = 0; i < Integer.valueOf(args[1]); i++) {
						Bukkit.broadcastMessage(" ");
					}
				}
				Bukkit.broadcastMessage(ChatColor.YELLOW + "The chat has been cleared by " + ChatColor.WHITE + sender.getName());	
			}
			else {
				sender.sendMessage(ChatColor.RED + "/chat clear <linesNumber>");
			}
		}
		if ((args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) && sender.hasPermission(this.main.getLoaderHandler().getPermission().getManageChat())) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("on")) {
					if (!this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
						sender.sendMessage(ChatColor.RED + "Sorry but the chat as already openned!");
						return false;
					}
					this.main.getManagerHandler().getServerManager().setChatState(ChatState.GLOBAL);
					sender.sendMessage(ChatColor.WHITE + "The " + ChatColor.RED + "chat" + ChatColor.WHITE + " has been " + ChatColor.GREEN + "enabled" + ChatColor.WHITE + "!");
					Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatOpened());
					return false;
				}	
				if (args[0].equalsIgnoreCase("off")) {
					if (this.main.getManagerHandler().getServerManager().getChatState().equals(ChatState.CLOSED)) {
						sender.sendMessage(ChatColor.RED + "Sorry but the chat as already closed!");
						return false;
					}
					this.main.getManagerHandler().getServerManager().setChatState(ChatState.CLOSED);
					sender.sendMessage(ChatColor.WHITE + "The " + ChatColor.RED + "chat" + ChatColor.WHITE + " has been " + ChatColor.RED + "closed" + ChatColor.WHITE + "!");
					Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatClosed());
					return false;
				}
			}
		}
		if (args[0].equalsIgnoreCase("priority") && sender.hasPermission(this.main.getLoaderHandler().getPermission().getManageChat())) {
			if (args.length == 2) {
				if (ChatPriority.valueOf(args[1].toUpperCase()) == null) {
					sender.sendMessage(ChatColor.RED + "/chat priority <HIGH/MEDIUM/LOWER/NORMAL/SPAM>");
					return false;
				}
				this.main.getManagerHandler().getServerManager().setChatPriority(ChatPriority.valueOf(args[1].toUpperCase()));
				Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getChatPriorityChange().replace("%priority%", args[1].toLowerCase()));
				return false;
			}
			sender.sendMessage(ChatColor.RED + "/chat priority <HIGH/MEDIUM/LOWER/NORMAL/SPAM>");
			return false;
		}
		return false;
	}

}

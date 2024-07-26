package akyto.core.handler.command.admin.rank;

import java.util.concurrent.ExecutionException;

import akyto.core.handler.loader.Message;
import akyto.core.handler.loader.Permission;
import akyto.core.handler.manager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import co.aikar.idb.DB;
import akyto.core.Core;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.database.DatabaseType;

public class RankCommand {

	private final Core main = Core.API;
	
	@Command(name = "rank", aliases= {"grade", "ranks"}, inGameOnly = false)
	public void rankCommand(final CommandArgs arg) {

		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		final RankManager rankManager = this.main.getManagerHandler().getRankManager();
		final Message message = this.main.getLoaderHandler().getMessage();
		final Permission perm = this.main.getLoaderHandler().getPermission();

		if (!sender.hasPermission(perm.getRankAdmin()) || !sender.hasPermission(perm.getRankUp()) || !sender.hasPermission(perm.getRankDown())) {
			sender.sendMessage(message.getNoPermission());
			return;
		}

		// HELP
		if (args.length == 0) {
			message.getRankHelp().forEach(sender::sendMessage);
			return;
		}

		if (args.length == 2) {

			// CREATE
			if (args[0].equalsIgnoreCase("create") && sender.hasPermission(perm.getRankAdmin())) {
				if (rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getAlreadyExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				rankManager.createRank(args[1].toLowerCase());
				sender.sendMessage(message.getCreated()
						.replace("%value%", args[1])
						.replace("%type%", "rank")
				);
				return;
			}

			// DELETE
			if (args[0].equalsIgnoreCase("delete") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				rankManager.getRanks().remove(args[1].toLowerCase());
				rankManager.getDeletedRank().add(args[1].toLowerCase());
				sender.sendMessage(message.getDeleted()
						.replace("%value%", args[1])
						.replace("%type%", "rank")
				);
				return;
			}
		}

		if (args.length == 3) {

			// PREFIX
			if (args[0].equalsIgnoreCase("setprefix") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				rankManager.getRanks().get(args[1].toLowerCase()).setPrefix(CoreUtils.translate(args[2]));
				sender.sendMessage(message.getEdited()
						.replace("%key%", args[1])
						.replace("%type%", "rank prefix")
						.replace("%value%", CoreUtils.translate(args[2]))
						.replace("%editType%", "updated")
				);
				return;
			}

			// COLOR
			if (args[0].equalsIgnoreCase("setcolor") && sender.hasPermission(perm.getRankAdmin())) {
				if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				ChatColor.translateAlternateColorCodes('&', args[2]);
				rankManager.getRanks().get(args[1].toLowerCase()).setColor(args[2]);
				sender.sendMessage(message.getEdited()
						.replace("%key%", args[1])
						.replace("%type%", "rank color")
						.replace("%value%", args[2])
						.replace("%editType%", "upgraded")
				);
				return;
			}

			// WHITELIST
			if (args[0].equalsIgnoreCase("setwhitelist") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				try {
					Boolean bool = Boolean.parseBoolean(args[2]);
					rankManager.getRanks().get(args[1].toLowerCase()).setCanJoinWhitelist(bool);
					sender.sendMessage(message.getEdited()
							.replace("%key%", args[1])
							.replace("%type%", "rank can join while whitelist")
							.replace("%value%", args[2]).replace("%editType%", "set to")
					);
				} catch (Exception e) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank can join while whitelist")
					);
					return;
				}
				return;
			}

			// POWER
			if (args[0].equalsIgnoreCase("setpower") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				try {
					int power = Integer.parseInt(args[2]);
					rankManager.getRanks().get(args[1].toLowerCase()).setPower(power);
					sender.sendMessage(message.getEdited()
							.replace("%key%", args[1])
							.replace("%type%", "rank power")
							.replace("%value%", args[2])
							.replace("%editType%", "set to")
					);
				} catch (Exception e) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank power")
					);
					return;
				}
				return;
			}

			// ADD - PERM
			if (args[0].equalsIgnoreCase("addperm") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				if (rankManager.getRanks().get(args[1].toLowerCase()).getPermissions().contains(args[2])) {
					sender.sendMessage(message.getAlreadySet()
							.replace("%value%", args[1])
							.replace("%type%", "rank permissions")
					);
					return;
				}
				rankManager.getRanks().get(args[1].toLowerCase()).getPermissions().add(args[2]);
				sender.sendMessage(message.getEdited()
						.replace("%key%", args[1])
						.replace("%type%", "rank permission")
						.replace("%editType%", "added")
						.replace("%value%", CoreUtils.translate(args[2]))
				);
				return;
			}

			// REMOVE - PERM
			if (args[0].equalsIgnoreCase("delperm") && sender.hasPermission(perm.getRankAdmin())) {
				if (!rankManager.getRanks().containsKey(args[1].toLowerCase())) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank")
					);
					return;
				}
				if (!rankManager.getRanks().get(args[1].toLowerCase()).getPermissions().contains(args[2])) {
					sender.sendMessage(message.getNotExist()
							.replace("%value%", args[1])
							.replace("%type%", "rank permissions")
					);
					return;
				}
				rankManager.getRanks().get(args[1].toLowerCase()).getPermissions().remove(args[2]);
				sender.sendMessage(message.getEdited()
						.replace("%key%", args[1])
						.replace("%type%", "rank permission")
						.replace("%editType%", "removed")
						.replace("%value%", CoreUtils.translate(args[2]))
				);
				return;
			}
		}
    }

}

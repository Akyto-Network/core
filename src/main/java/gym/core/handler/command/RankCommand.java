package gym.core.handler.command;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import co.aikar.idb.DB;
import gym.core.Core;
import gym.core.rank.RankType;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import gym.core.utils.database.DatabaseType;

public class RankCommand {

	private Core main = Core.API;
	
	@Command(name = "rank", aliases= {"grade", "ranks"}, inGameOnly = true)
	public void rankCommand(final CommandArgs arg) {
		final CommandSender sender = arg.getSender();
		final String[] args = arg.getArgs();
		if (!sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin()) || !sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankUp()) || !sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankDown())) {
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getNoPermission());
			return;
		}
		if (args.length == 0) {
			this.main.getLoaderHandler().getMessage().getRankHelp().forEach(sender::sendMessage);
			return;
		}
		if (args[0].equalsIgnoreCase("create") && args.length == 2 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getAlreadyExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			this.main.getManagerHandler().getRankManager().createRank(args[1].toLowerCase());
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getCreated().replace("%value%", args[1]).replace("%type%", "rank"));
			return;
		}
		if (args[0].equalsIgnoreCase("setprefix") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).setPrefix(Utils.translate(args[2]));
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getEdited().replace("%key%", args[1]).replace("%type%", "rank prefix").replace("%value%", Utils.translate(args[2])).replace("%editType%", "updated"));
			return;
		}
		if (args[0].equalsIgnoreCase("setcolor") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			if (ChatColor.translateAlternateColorCodes('&', args[2]) == null) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank color"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).setColor(args[2]);
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getEdited().replace("%key%", args[1]).replace("%type%", "rank color").replace("%value%", args[2]).replace("%editType%", "upgraded"));
			return;
		}
		if (args[0].equalsIgnoreCase("setranktype") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			if (RankType.valueOf(args[2].toUpperCase()) == null) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank type"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).setRankType(RankType.valueOf(args[2].toUpperCase()));
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getEdited().replace("%key%", args[1]).replace("%type%", "rank color").replace("%value%", args[2]).replace("%editType%", "setted to"));
			return;
		}
		if (args[0].equalsIgnoreCase("addperm") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			if (this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).getPermissions().contains(args[2])) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getAlreadySet().replace("%value%", args[1]).replace("%type%", "rank permissions"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).getPermissions().add(args[2]);
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getEdited().replace("%key%", args[1]).replace("%type%", "rank permission").replace("%editType%", "added").replace("%value%", Utils.translate(args[2])));
			return;
		}
		if (args[0].equalsIgnoreCase("delperm") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			if (!this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).getPermissions().contains(args[2])) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank permissions"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().get(args[1].toLowerCase()).getPermissions().remove(args[2]);
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getEdited().replace("%key%", args[1]).replace("%type%", "rank permission").replace("%editType%", "removed").replace("%value%", Utils.translate(args[2])));
			return;
		}
		if (args[0].equalsIgnoreCase("delete") && args.length == 2 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankAdmin())) {
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[1].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[1]).replace("%type%", "rank"));
				return;
			}
			this.main.getManagerHandler().getRankManager().getRanks().remove(args[1].toLowerCase());
			this.main.getManagerHandler().getRankManager().getDeletedRank().add(args[1].toLowerCase());
			sender.sendMessage(this.main.getLoaderHandler().getMessage().getDeleted().replace("%value%", args[1]).replace("%type%", "rank"));
			return;
		}
		if (args[0].equalsIgnoreCase("promote") && args.length == 3 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankUp())) {
			if (this.main.getDatabaseType().equals(DatabaseType.FLAT_FILES) && !this.main.getManagerHandler().getProfileManager().getProfiles().containsKey(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNeverPlayed().replace("%target%", args[1]));
				return;
			}
			if (!this.main.getManagerHandler().getRankManager().getRanks().containsKey(args[2].toLowerCase())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNotExist().replace("%value%", args[2]).replace("%type%", "rank"));
				return;
			}
			if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
				try {
					if (Bukkit.getPlayer(args[1]) == null && this.main.getMySQL().existPlayerManagerAsync(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).get()) {
						DB.executeUpdateAsync("UPDATE coredata SET rank=? WHERE name=?", args[2].toLowerCase() , args[1]);
					}
				} catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
			}
			if (this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId()) != null) {
				this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId()).setRank(args[2].toLowerCase());	
			}	
			Bukkit.getPlayer(args[1]).setPlayerListName(Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]).getUniqueId()).getRank()).getColor()) + Bukkit.getPlayer(args[1]).getName().substring(0, Math.min(Bukkit.getPlayer(args[1]).getName().length(), 15)));
			if (this.main.getLoaderHandler().getSettings().isRankPromoteBroad()) {
				Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getRankUp().replace("%target%", args[1]).replace("%rankUp%", args[2]));
			}
			
		}
		if (args[0].equalsIgnoreCase("demote") && args.length == 2 && sender.hasPermission(this.main.getLoaderHandler().getPermission().getRankUp())) {
			if (this.main.getDatabaseType().equals(DatabaseType.FLAT_FILES) && !this.main.getManagerHandler().getProfileManager().getProfiles().containsKey(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
				sender.sendMessage(this.main.getLoaderHandler().getMessage().getNeverPlayed().replace("%target%", args[1]));
				return;
			}
			if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
				try {
					if (Bukkit.getPlayer(args[1]) == null && this.main.getMySQL().existPlayerManagerAsync(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).get()) {
						DB.executeUpdateAsync("UPDATE coredata SET rank=? WHERE name=?", "default" , args[1]);
					}
				} catch (InterruptedException e) { e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace(); }
			}
			if (this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId()) != null) {
				this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId()).setRank("default");	
			}	
			if (Bukkit.getPlayer(args[1]) != null) {
				Bukkit.getPlayer(args[1]).setPlayerListName(Utils.translate(this.main.getManagerHandler().getRankManager().getRanks().get(this.main.getManagerHandler().getProfileManager().getProfiles().get(Bukkit.getPlayer(args[1]).getUniqueId()).getRank()).getColor()) + Bukkit.getPlayer(args[1]).getName().substring(0, Math.min(Bukkit.getPlayer(args[1]).getName().length(), 15)));
			}
			if (this.main.getLoaderHandler().getSettings().isRankDemoteBroad()) {
				Bukkit.broadcastMessage(this.main.getLoaderHandler().getMessage().getRankDown().replace("%target%", args[1]));
			}
			
		}
		return;
	}

}

package akyto.core.utils.database;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import akyto.core.rank.RankEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.aikar.idb.BukkitDB;
import co.aikar.idb.DB;
import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.format.FormatUtils;

public class DatabaseSetup {
	
	private final Core main;
	
	public DatabaseSetup(final Core main) {
		this.main = main;
	}
	
	public void exitAsync(final UUID uuid) {
		CompletableFuture<Void> exit = CompletableFuture.runAsync(() -> this.exit(uuid));
		exit.whenCompleteAsync((t, u) -> {
			this.main.getManagerHandler().getProfileManager().getProfiles().remove(uuid);
		});
	}

	public void exit(final UUID uuid) {
		final Profile data = this.main.getManagerHandler().getProfileManager().getProfiles().get(uuid);
		if (data != null) {
			String playerName = CoreUtils.getName(uuid);
			if (Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().containsKey(playerName)){
				playerName = Core.API.getManagerHandler().getProfileManager().getRealNameInDisguised().get(playerName);
			}
            try {
                DB.executeUpdate("UPDATE playersdata SET scoreboard=?, duelRequest=?, time=?, displaySpectate=?, flySpeed=?, played=?, win=?, WHERE name=?",
                        Boolean.toString(data.getSettings().get(0)),
                        Boolean.toString(data.getSettings().get(1)),
                        Boolean.toString(data.getSettings().get(2)),
                        Boolean.toString(data.getSpectateSettings().get(0)),
                        Boolean.toString(data.getSpectateSettings().get(1)),
                        FormatUtils.getStringValue(data.getStats().get(0), ":"),
                        FormatUtils.getStringValue(data.getStats().get(1), ":"),
                        playerName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
	}

	public void resetElos(final String name, final int[] elos, final int[] win, final int[] played) {
		try {
			DB.executeUpdate("UPDATE playersdata SET played=? WHERE name=?", FormatUtils.getStringValue(played, ":") , name);
			DB.executeUpdate("UPDATE playersdata SET win=? WHERE name=?", FormatUtils.getStringValue(win, ":") , name);
			DB.executeUpdate("UPDATE playersdata SET elos=? WHERE name=?", FormatUtils.getStringValue(elos, ":") , name);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void update(final UUID uuid, final int kitSize, final String[] kitNames) {
	    try {
	        Player player = Bukkit.getPlayer(uuid);
	        if (player == null) return;
	        if (!this.main.getMySQL().existPlayerManagerAsync(uuid).get()) {
	            this.main.getMySQL().createPlayerManagerAsync(uuid, player.getName());
	            return;
	        }
	        if (this.main.getMySQL().existPlayerManagerAsync(uuid).get()) {
	            this.main.getMySQL().updatePlayerManagerAsync(player.getName(), uuid);
	        }
            this.loadAsync(uuid, kitSize, kitNames);
	    } catch (InterruptedException | ExecutionException e) {
	        e.printStackTrace();
	    }
	}
	
	public void loadAsync(final UUID uuid, final int kitSize, final String[] kitNames) {
		CompletableFuture<Void> load = CompletableFuture.runAsync(() -> this.load(uuid));
		load.whenCompleteAsync((t, u) -> {
			if (kitSize != 0 && kitNames != null){
				Core.API.getManagerHandler().getInventoryManager().generateProfileInventory(uuid, kitSize, kitNames);
			}
			this.main.getManagerHandler().getProfileManager().registerPermissions(uuid);
			final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(uuid);
			final Player player = Bukkit.getPlayer(uuid);
			final String name = player.getName().substring(0, Math.min(player.getName().length(), 14));
			player.setPlayerListName(CoreUtils.translate(rank.getColor()) + name);
			if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
				Bukkit.getOnlinePlayers().forEach(players -> {
					if (players.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())
							&& !rank.equals(Core.API.getManagerHandler().getRankManager().getRanks().get("default"))
							&& player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {

						players.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce()
								.replace("%rank%", rank.getPrefix())
								.replace("%rankColor%", CoreUtils.translate(rank.getColor()))
								.replace("%player%", CoreUtils.getName(uuid))
								.replace("%type%", "join"));
					}
				});
			}
		});
	}
	
	public void load(final UUID uuid) {
		final Profile data = this.main.getManagerHandler().getProfileManager().getProfiles().get(uuid);
		final String playerName = CoreUtils.getName(uuid);
		try {
			data.getSettings().set(0, Boolean.valueOf(DB.getFirstRow("SELECT scoreboard FROM playersdata WHERE name=?", playerName).getString("scoreboard")));
			data.getSettings().set(1, Boolean.valueOf(DB.getFirstRow("SELECT duelRequest FROM playersdata WHERE name=?", playerName).getString("duelRequest")));
			data.getSettings().set(2, Boolean.valueOf(DB.getFirstRow("SELECT time FROM playersdata WHERE name=?", playerName).getString("time")));
			data.getSpectateSettings().set(0, Boolean.valueOf(DB.getFirstRow("SELECT flySpeed FROM playersdata WHERE name=?", playerName).getString("flySpeed")));
			data.getSpectateSettings().set(1, Boolean.valueOf(DB.getFirstRow("SELECT displaySpectate FROM playersdata WHERE name=?", playerName).getString("displaySpectate")));
			data.getStats().set(2, FormatUtils.getSplitValue(DB.getFirstRow("SELECT elos FROM playersdata WHERE name=?", playerName).getString("elos"), ":"));
			data.getStats().set(1, FormatUtils.getSplitValue(DB.getFirstRow("SELECT win FROM playersdata WHERE name=?", playerName).getString("win"), ":"));
			data.getStats().set(0, FormatUtils.getSplitValue(DB.getFirstRow("SELECT played FROM playersdata WHERE name=?", playerName).getString("played"), ":"));
			data.setRank(DB.getFirstRow("SELECT rank FROM playersdata WHERE name=?", playerName).getString("rank"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

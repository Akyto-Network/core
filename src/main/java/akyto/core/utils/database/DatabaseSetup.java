package akyto.core.utils.database;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import akyto.core.handler.ManagerHandler;
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
		this.setupHikariCP();
		this.setupDatabase();
	}
	
    public void setupDatabase() {
        if (this.main.connection != null) {
        	this.main.getMySQL().createPlayerManagerTableAsync();
            return;
        }
        System.out.println("WARNING enter valid database information (" + this.main.getHikariPath() + ") \n You will not be able to access many features");
    }
    
    public void setupHikariCP() {
        try {
            final HikariConfig config = new HikariConfig(this.main.getHikariPath());
            @SuppressWarnings("resource")
			final HikariDataSource ds = new HikariDataSource(config);
            final String passwd = (config.getDataSourceProperties().getProperty("password") == null) ? "" : config.getDataSourceProperties().getProperty("password");
            BukkitDB.createHikariDatabase(this.main, config.getDataSourceProperties().getProperty("user"), passwd, config.getDataSourceProperties().getProperty("databaseName"), config.getDataSourceProperties().getProperty("serverName") + ":" + config.getDataSourceProperties().getProperty("portNumber"));
            this.main.connection = ds.getConnection();
        }
        catch (SQLException e) {
            System.out.println("Error could not connect to SQL database.");
            e.printStackTrace();
        }
        System.out.println("Successfully connected to the SQL database.");
    }
	
	public void exitAsync(final UUID uuid) {
		final Profile data = this.main.getManagerHandler().getProfileManager().getProfiles().get(uuid);
		if (data != null) {
			final String playerName = CoreUtils.getName(uuid);
			DB.executeUpdateAsync("UPDATE playersdata SET scoreboard=?, duelRequest=?, time=?, displaySpectate=?, flySpeed=?, played=?, win=?, rank=? WHERE name=?",
					String.valueOf(data.getSettings().get(0).booleanValue()),
					String.valueOf(data.getSettings().get(1).booleanValue()),
					String.valueOf(data.getSettings().get(2).booleanValue()),
					String.valueOf(data.getSpectateSettings().get(0).booleanValue()),
					String.valueOf(data.getSpectateSettings().get(1).booleanValue()),
					FormatUtils.getStringValue(data.getStats().get(0), ":"),
					FormatUtils.getStringValue(data.getStats().get(1), ":"),
					data.getRank(),
					playerName).join();
		}
		this.main.getManagerHandler().getProfileManager().getProfiles().remove(uuid);
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
		final ManagerHandler managerHandler = Core.API.getManagerHandler();
		load.whenCompleteAsync((t, u) -> {
			managerHandler.getInventoryManager().generateProfileInventory(uuid, kitSize, kitNames);
			managerHandler.getProfileManager().registerPermissions(uuid);
			Bukkit.getPlayer(uuid).setPlayerListName(CoreUtils.translate(this.main.getManagerHandler().getProfileManager().getRank(uuid).getColor()) + Bukkit.getPlayer(uuid).getName().substring(0, Math.min(Bukkit.getPlayer(uuid).getName().length(), 15)));
			if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
				final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(uuid);
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())
							&& !rank.equals(Core.API.getManagerHandler().getRankManager().getRanks().get("default"))
							&& Bukkit.getPlayer(uuid).hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {

						player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce()
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
			Map<String, Object> row = DB.getFirstRow("SELECT scoreboard, duelRequest, time, displaySpectate, flySpeed, played, win, elos, rank FROM playersdata WHERE name=?", playerName);

			data.getSettings().set(0, Boolean.valueOf((String) row.get("scoreboard")));
			data.getSettings().set(1, Boolean.valueOf((String) row.get("duelRequest")));
			data.getSettings().set(2, Boolean.valueOf((String) row.get("time")));
			data.getSpectateSettings().set(0, Boolean.valueOf((String) row.get("displaySpectate")));
			data.getSpectateSettings().set(1, Boolean.valueOf((String) row.get("flySpeed")));
			data.getStats().set(2, FormatUtils.getSplitValue((String) row.get("elos"), ":"));
			data.getStats().set(1, FormatUtils.getSplitValue((String) row.get("win"), ":"));
			data.getStats().set(0, FormatUtils.getSplitValue((String) row.get("played"), ":"));
			data.setRank((String) row.get("rank"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

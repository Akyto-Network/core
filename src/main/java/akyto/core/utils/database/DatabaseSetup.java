package akyto.core.utils.database;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    
	public void closeConnection() {
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				final Profile data = this.main.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId());
		        try {
			        DB.executeUpdate("UPDATE playersdata SET scoreboard=? WHERE name=?", String.valueOf(data.getSettings().get(0).booleanValue()), player.getName());
			        DB.executeUpdate("UPDATE playersdata SET duelRequest=? WHERE name=?", String.valueOf(data.getSettings().get(1).booleanValue()), player.getName());
			        DB.executeUpdate("UPDATE playersdata SET time=? WHERE name=?", String.valueOf(data.getSettings().get(2).booleanValue()), player.getName());
			        DB.executeUpdate("UPDATE playersdata SET displaySpectate=? WHERE name=?", String.valueOf(data.getSpectateSettings().get(0).booleanValue()), player.getName());
			        DB.executeUpdate("UPDATE playersdata SET flySpeed=? WHERE name=?", String.valueOf(data.getSpectateSettings().get(1).booleanValue()), player.getName());
			        DB.executeUpdate("UPDATE playersdata SET played=? WHERE name=?", FormatUtils.getStringValue(data.getStats().get(0), ":"), player.getName());
			    	DB.executeUpdate("UPDATE playersdata SET win=? WHERE name=?", FormatUtils.getStringValue(data.getStats().get(1), ":"), player.getName());
				} catch (SQLException e) { e.printStackTrace(); }
			});
		}
	}
	
	public void exitAsync(final UUID uuid) {
		final Profile data = this.main.getManagerHandler().getProfileManager().getProfiles().get(uuid);
		if (data != null) {
			final String playerName = CoreUtils.getName(uuid);
	        DB.executeUpdateAsync("UPDATE playersdata SET scoreboard=? WHERE name=?", String.valueOf(data.getSettings().get(0).booleanValue()), playerName).join();
	        DB.executeUpdateAsync("UPDATE playersdata SET duelRequest=? WHERE name=?", String.valueOf(data.getSettings().get(1).booleanValue()), playerName).join();
	        DB.executeUpdateAsync("UPDATE playersdata SET time=? WHERE name=?", String.valueOf(data.getSettings().get(2).booleanValue()), playerName).join();
	        DB.executeUpdateAsync("UPDATE playersdata SET displaySpectate=? WHERE name=?", String.valueOf(data.getSpectateSettings().get(0).booleanValue()), playerName).join();
	        DB.executeUpdateAsync("UPDATE playersdata SET flySpeed=? WHERE name=?", String.valueOf(data.getSpectateSettings().get(1).booleanValue()), playerName).join();
	        DB.executeUpdateAsync("UPDATE playersdata SET played=? WHERE name=?", FormatUtils.getStringValue(data.getStats().get(0), ":"), playerName).join();
	    	DB.executeUpdateAsync("UPDATE playersdata SET win=? WHERE name=?", FormatUtils.getStringValue(data.getStats().get(1), ":"), playerName).join();
			DB.executeUpdateAsync("UPDATE playersdata SET rank=? WHERE name=?", data.getRank(), playerName).join();
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
		load.whenCompleteAsync((t, u) -> {
			Core.API.getManagerHandler().getInventoryManager().generateProfileInventory(uuid, kitSize, kitNames);
			this.main.getManagerHandler().getProfileManager().registerPermissions(uuid);
			Bukkit.getPlayer(uuid).setPlayerListName(CoreUtils.translate(this.main.getManagerHandler().getProfileManager().getRank(uuid).getColor()) + Bukkit.getPlayer(uuid).getName().substring(0, Math.min(Bukkit.getPlayer(uuid).getName().length(), 15)));
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

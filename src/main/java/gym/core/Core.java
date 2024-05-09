package gym.core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import gym.core.handler.CommandHandler;
import gym.core.handler.LoaderHandler;
import gym.core.handler.ManagerHandler;
import gym.core.handler.listener.PlayerListener;
import gym.core.punishment.BanEntry;
import gym.core.punishment.MuteEntry;
import gym.core.punishment.file.PunishmentFile;
import gym.core.rank.RankEntry;
import gym.core.rank.file.RankFile;
import gym.core.runnable.CheckRunnable;
import gym.core.runnable.VerifRunnable;
import gym.core.utils.Utils;
import gym.core.utils.database.DatabaseSetup;
import gym.core.utils.database.DatabaseType;
import gym.core.utils.database.api.MySQL;
import kezukdev.akyto.Practice;
import lombok.Getter;

@Getter
public class Core extends JavaPlugin {
	
	public static Core API;
	
	@Getter
    private String hikariPath;
	private String namemcURL;
    public Connection connection;
	private DatabaseType databaseType;
	private LoaderHandler loaderHandler;
	private RankFile rankFile;
	private PunishmentFile punishmentFile;
	private ManagerHandler managerHandler;
	private CommandHandler commandHandler;
	private MySQL mySQL;
	private Practice practiceAPI;
	
	public void onEnable() {
		API = this;
		this.saveDefaultConfig();
		if (this.getConfig().getString("bungeecord.enable").equalsIgnoreCase("true")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}
		this.databaseType = DatabaseType.valueOf(this.getConfig().getString("database.type")) != null ? DatabaseType.valueOf(this.getConfig().getString("database.type")) : DatabaseType.FLAT_FILES;
		if (databaseType.equals(DatabaseType.MYSQL)) {
			this.hikariPath = this.getDataFolder() + "/hikari.properties";
			this.saveResource("hikari.properties", false);
			this.mySQL = new MySQL(this);
			new DatabaseSetup(this);
		}
		this.practiceAPI = Practice.getAPI();
		this.registerListener();
		this.namemcURL = this.getConfig().getString("namemc.server-ip");
		this.loaderHandler = new LoaderHandler(this);
		this.managerHandler = new ManagerHandler(this);
		this.rankFile = new RankFile(this);
		this.punishmentFile = new PunishmentFile(this);
		this.commandHandler = new CommandHandler(this);
		new VerifRunnable().runTaskTimerAsynchronously(this, 0L, 1L);
		new CheckRunnable(this, this.getConfig().getInt("autoclicker.max-cps"), this.getConfig().getString("autoclicker.alert-message")).runTaskTimerAsynchronously(this, 0L, 20L);
	}

	private void registerListener() {
        for (Listener listener : Arrays.asList(new PlayerListener(this))) {
        	this.getServer().getPluginManager().registerEvents(listener, this);
        }
	}

	public void onDisable() {
		this.saveRank();
		this.savePunishment();
		this.managerHandler.getRankManager().getRanks().clear();
		this.saveDatabase();
		try { this.getRankFile().getConfig().save(this.getRankFile().getFile()); } catch (IOException e) { e.printStackTrace(); }
		try { this.getPunishmentFile().getConfig().save(this.getPunishmentFile().getFile()); } catch (IOException e) { e.printStackTrace(); }
	}
	
	private void savePunishment() {
		for (Entry<UUID, BanEntry> ban : this.managerHandler.getPunishmentManager().getBanned().entrySet()) {
			if (this.getPunishmentFile().getConfig().getConfigurationSection("banned." + ban.getKey()) == null) {
				this.getPunishmentFile().getConfig().createSection("banned." + ban.getKey());
				this.getPunishmentFile().getConfig().createSection("banned." + ban.getKey() + ".judge");
				this.getPunishmentFile().getConfig().set("banned." + ban.getKey() + ".judge", ban.getValue().getJudge());
				this.getPunishmentFile().getConfig().createSection("banned." + ban.getKey() + ".reason");
				this.getPunishmentFile().getConfig().set("banned." + ban.getKey() + ".reason", ban.getValue().getReason());
				this.getPunishmentFile().getConfig().createSection("banned." + ban.getKey() + ".expires");
				this.getPunishmentFile().getConfig().set("banned." + ban.getKey() + ".expires", ban.getValue().getExpiresOn());
			}
		}
		this.managerHandler.getPunishmentManager().getUnbanned().forEach(bannedName -> {
			final String uuidStr = String.valueOf(bannedName);
			if (this.getPunishmentFile().getConfig().getConfigurationSection("banned." + uuidStr) != null) {
				this.getPunishmentFile().getConfig().set("banned." + uuidStr + ".judge", null);
				this.getPunishmentFile().getConfig().set("banned." + uuidStr + ".reason", null);
				this.getPunishmentFile().getConfig().set("banned." + uuidStr + ".expires", null);
				this.getPunishmentFile().getConfig().set("banned." + uuidStr, null);
			}
		});
		for (Entry<UUID, MuteEntry> ban : this.managerHandler.getPunishmentManager().getMuted().entrySet()) {
			if (this.getPunishmentFile().getConfig().getConfigurationSection("muted." + ban.getKey()) == null) {
				this.getPunishmentFile().getConfig().createSection("muted." + ban.getKey());
				this.getPunishmentFile().getConfig().createSection("muted." + ban.getKey() + ".judge");
				this.getPunishmentFile().getConfig().set("muted." + ban.getKey() + ".judge", ban.getValue().getJudge());
				this.getPunishmentFile().getConfig().createSection("muted." + ban.getKey() + ".reason");
				this.getPunishmentFile().getConfig().set("muted." + ban.getKey() + ".reason", ban.getValue().getReason());
				this.getPunishmentFile().getConfig().createSection("muted." + ban.getKey() + ".expires");
				this.getPunishmentFile().getConfig().set("muted." + ban.getKey() + ".expires", ban.getValue().getExpiresOn());
			}
		}
		this.managerHandler.getPunishmentManager().getUnmuted().forEach(bannedName -> {
			final String uuidStr = String.valueOf(bannedName);
			if (this.getPunishmentFile().getConfig().getConfigurationSection("muted." + uuidStr) != null) {
				this.getPunishmentFile().getConfig().set("muted." + uuidStr + ".judge", null);
				this.getPunishmentFile().getConfig().set("muted." + uuidStr + ".reason", null);
				this.getPunishmentFile().getConfig().set("muted." + uuidStr + ".expires", null);
				this.getPunishmentFile().getConfig().set("muted." + uuidStr, null);
			}
		});
	}

	private void saveRank() {
		for (Entry<String, RankEntry> rank : this.managerHandler.getRankManager().getRanks().entrySet()) {
			if (this.getRankFile().getConfig().getConfigurationSection("ranks." + rank.getKey()) == null) {
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey());
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".prefix");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".prefix", rank.getValue().getPrefix());
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".color");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".color", rank.getValue().getColor());
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".spaceBetweenPrefixAndColor");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".color", String.valueOf(rank.getValue().getHasSpaceBetweenColor().booleanValue()));
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".permissions");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".permissions", rank.getValue().getPermissions());
			}
		}
		this.managerHandler.getRankManager().getDeletedRank().forEach(rankName -> {
			if (this.getRankFile().getConfig().getConfigurationSection("ranks." + rankName) != null) {
				this.getRankFile().getConfig().set("ranks." + rankName + ".prefix", null);
				this.getRankFile().getConfig().set("ranks." + rankName + ".color", null);
				this.getRankFile().getConfig().set("ranks." + rankName + ".spaceBetweenPrefixAndColor", null);
				this.getRankFile().getConfig().set("ranks." + rankName + ".permissions", null);
				this.getRankFile().getConfig().set("ranks." + rankName, null);
			}
		});
	}

	private void saveDatabase() {
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				if (this.getConfig().getBoolean("bungeecord.move-to-hub-at-restart")) {
					player.sendMessage(Utils.translate(this.getConfig().getString("messages.server-restart")));
					Utils.sendServer(player, "Connect", this.getConfig().getString("bungeecord.hub-instance"));
				}
				else if (!this.getConfig().getBoolean("bungeecord.move-to-hub-at-restart")) {
					player.kickPlayer(Utils.translate(this.getConfig().getString("messages.server-restart")));	
				}
			});
		}
		if (this.databaseType.equals(DatabaseType.FLAT_FILES)) {
			if (!this.managerHandler.getProfileManager().getProfiles().isEmpty()) {
				for (UUID uuid : this.managerHandler.getProfileManager().getProfiles().keySet()) {
					File file = new File(getDataFolder() + "/players/" + uuid.toString() + ".yml");
					if (!file.exists()) {
						try {
			                file.getParentFile().mkdirs();
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
					configFile.createSection("rank");
					configFile.set("rank", this.getManagerHandler().getProfileManager().getProfiles().get(uuid).getRank());
					try {
						configFile.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
				System.out.println("[CORE - Profiles] Flat-Files saved!");
			}
		}
	}
}

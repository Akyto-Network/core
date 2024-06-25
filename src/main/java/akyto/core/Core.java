package akyto.core;

import akyto.core.handler.CommandHandler;
import akyto.core.handler.LoaderHandler;
import akyto.core.handler.ManagerHandler;
import akyto.core.handler.listener.PlayerListener;
import akyto.core.handler.listener.TabListListener;
import akyto.core.punishment.cache.BanEntry;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.punishment.file.PunishmentFile;
import akyto.core.rank.RankEntry;
import akyto.core.rank.file.RankFile;
import akyto.core.runnable.TipsRunnable;
import akyto.core.utils.CoreUtils;
import akyto.spigot.aSpigot;
import akyto.core.utils.database.DatabaseSetup;
import akyto.core.utils.database.DatabaseType;
import akyto.core.utils.database.api.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map.Entry;
import java.util.UUID;

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
	private DatabaseSetup databaseSetup;
	private boolean debug;
	
	public void onEnable() {
		API = this;
		debug = getServer().getOptions().has("debug");
		if (debug)
			getLogger().info("Debug mode enabled");
        new TipsRunnable().runTaskTimerAsynchronously(this, 6000L, 6000L);
		this.saveDefaultConfig();
		if (this.getConfig().getString("bungeecord.enable").equalsIgnoreCase("true")) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}
        this.databaseType = DatabaseType.valueOf(this.getConfig().getString("database.type"));
		if (databaseType.equals(DatabaseType.MYSQL)) {
			this.hikariPath = this.getDataFolder() + "/hikari.properties";
			this.saveResource("hikari.properties", false);
			this.mySQL = new MySQL(this);
			this.databaseSetup = new DatabaseSetup(this);
		}
		this.registerListener();
		this.namemcURL = this.getConfig().getString("namemc.server-ip");
		this.loaderHandler = new LoaderHandler(this);
		this.managerHandler = new ManagerHandler(this);
		this.rankFile = new RankFile(this);
		this.punishmentFile = new PunishmentFile(this);
		this.commandHandler = new CommandHandler(this);
	}

	private void registerListener() {
		if (debug)
			aSpigot.INSTANCE.addPacketHandler(new TabListListener());
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
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
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".spaceBetweenPrefixAndColor", String.valueOf(rank.getValue().hasSpaceBetweenColor().booleanValue()));
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".permissions");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".permissions", rank.getValue().getPermissions());
			}
			if (this.getRankFile().getConfig().getConfigurationSection("ranks." + rank.getKey()) != null) {
				rank.getValue().getPermissions().forEach(str -> {
					if (!this.getRankFile().getConfig().getString("ranks." + rank.getKey() + ".prefix").equals(rank.getValue().getPrefix())) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".prefix", rank.getValue().getPrefix());
					}
					if (!this.getRankFile().getConfig().getString("ranks." + rank.getKey() + ".color").equals(rank.getValue().getColor())) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".color", rank.getValue().getColor());
					}
					if (!this.getRankFile().getConfig().getString("ranks." + rank.getKey() + ".spaceBetweenPrefixAndColor").equals(String.valueOf(rank.getValue().hasSpaceBetweenColor().booleanValue()))) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".spaceBetweenPrefixAndColor", String.valueOf(rank.getValue().hasSpaceBetweenColor().booleanValue()));
					}
					if (!this.getRankFile().getConfig().getStringList("ranks." + rank.getKey() + ".permissions").contains(str)) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".permissions", rank.getValue().getPermissions());
					}
				});
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
				player.kickPlayer(CoreUtils.translate(this.getConfig().getString("messages.server-restart")));
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
				getLogger().info("[CORE - Profiles] Flat-Files saved!");
			}
		}
		if (this.databaseType.equals(DatabaseType.MYSQL)) {
			this.getDatabaseSetup().closeConnection();
		}
	}
}

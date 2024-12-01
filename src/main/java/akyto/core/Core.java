package akyto.core;

import akyto.core.disguise.file.DisguiseFile;
import akyto.core.handler.CommandHandler;
import akyto.core.handler.LoaderHandler;
import akyto.core.handler.ManagerHandler;
import akyto.core.handler.listener.PlayerListener;
import akyto.core.particle.ParticleEntry;
import akyto.core.particle.file.ParticlesFile;
import akyto.core.punishment.cache.BanEntry;
import akyto.core.punishment.cache.BlacklistEntry;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.punishment.file.PunishmentFile;
import akyto.core.rank.RankEntry;
import akyto.core.rank.file.RankFile;
import akyto.core.runnable.TipsRunnable;
import akyto.core.tag.file.TagFile;
import akyto.core.utils.database.DatabaseSetup;
import akyto.core.utils.database.DatabaseType;
import akyto.core.utils.database.api.MySQL;
import co.aikar.idb.BukkitDB;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
	private ParticlesFile particlesFile;
	private DisguiseFile disguiseFile;
	private TagFile tagFile;
	private PunishmentFile punishmentFile;
	private ManagerHandler managerHandler;
	private CommandHandler commandHandler;
	private MySQL mySQL;
	private Jedis redis;
	private DatabaseSetup databaseSetup;
	private boolean debug;
	private boolean shutdown = false;
	private HikariDataSource dataSource;
	@Getter
	private final List<String> bypassCpsCap = Lists.newArrayList();
	@Getter
	private final List<String> whitelisted = Lists.newArrayList();
	@Getter
	private final List<String> blacklistWhitelist = Lists.newArrayList();
	@Getter
	private final List<ParticleEntry> particles = Lists.newArrayList();
	
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
			this.redis = new Jedis("localhost", 6379);
			this.redis.auth(getConfig().getString("database.redis-password"));
			this.hikariPath = this.getDataFolder() + "/hikari.properties";
			this.saveResource("hikari.properties", false);
			this.setupHikariCP();
			this.mySQL = new MySQL(this);
			this.setupDatabase();
			this.databaseSetup = new DatabaseSetup(this);
		}
		this.registerListener();
		this.namemcURL = this.getConfig().getString("namemc.server-ip");
		this.loaderHandler = new LoaderHandler(this);
		this.particlesFile = new ParticlesFile(this);
		this.managerHandler = new ManagerHandler(this);
		this.tagFile = new TagFile(this);
		this.rankFile = new RankFile(this);
		this.disguiseFile = new DisguiseFile(this);
		this.punishmentFile = new PunishmentFile(this);
		this.commandHandler = new CommandHandler(this);
	}

	private void registerListener() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}

	public void onDisable() {
		this.saveConfig();
		this.saveDatabase();
		this.saveRank();
		this.savePunishment();
		this.getConfig().set("autoclicker.bypass", this.getBypassCpsCap());
		this.getConfig().set("whitelist.state", this.getManagerHandler().getServerManager().getWhitelistState().toString());
		this.getConfig().set("whitelist.allowed", this.getWhitelisted());
		this.getConfig().set("whitelist.blacklist", this.getBlacklistWhitelist());
		this.managerHandler.getRankManager().getRanks().clear();
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
		for (Entry<UUID, BlacklistEntry> ban : this.managerHandler.getPunishmentManager().getBlacklisted().entrySet()) {
			if (this.getPunishmentFile().getConfig().getConfigurationSection("blacklisted." + ban.getKey()) == null) {
				this.getPunishmentFile().getConfig().createSection("blacklisted." + ban.getKey());
				this.getPunishmentFile().getConfig().createSection("blacklisted." + ban.getKey() + ".judge");
				this.getPunishmentFile().getConfig().set("blacklisted." + ban.getKey() + ".judge", ban.getValue().getJudge());
				this.getPunishmentFile().getConfig().createSection("blacklisted." + ban.getKey() + ".reason");
				this.getPunishmentFile().getConfig().set("blacklisted." + ban.getKey() + ".reason", ban.getValue().getReason());
				this.getPunishmentFile().getConfig().createSection("blacklisted." + ban.getKey() + ".ip");
				this.getPunishmentFile().getConfig().set("blacklisted." + ban.getKey() + ".expires", ban.getValue().getIp());
			}
		}
		this.managerHandler.getPunishmentManager().getUnblacklisted().forEach(bannedName -> {
			final String uuidStr = String.valueOf(bannedName);
			if (this.getPunishmentFile().getConfig().getConfigurationSection("blacklisted." + uuidStr) != null) {
				this.getPunishmentFile().getConfig().set("blacklisted." + uuidStr + ".judge", null);
				this.getPunishmentFile().getConfig().set("blacklisted." + uuidStr + ".reason", null);
				this.getPunishmentFile().getConfig().set("blacklisted." + uuidStr + ".ip", null);
				this.getPunishmentFile().getConfig().set("blacklisted." + uuidStr, null);
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
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".whitelist");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".whitelist", String.valueOf(rank.getValue().hasRankWhitelist().booleanValue()));
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".permissions");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".permissions", rank.getValue().getPermissions());
				this.getRankFile().getConfig().createSection("ranks." + rank.getKey() + ".power");
				this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".power", rank.getValue().getPower());
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
					if (!this.getRankFile().getConfig().getString("ranks." + rank.getKey() + ".whitelist").equals(String.valueOf(rank.getValue().hasRankWhitelist().booleanValue()))) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".whitelist", String.valueOf(rank.getValue().hasRankWhitelist().booleanValue()));
					}
					if (!this.getRankFile().getConfig().getStringList("ranks." + rank.getKey() + ".permissions").contains(str)) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".permissions", rank.getValue().getPermissions());
					}
					if (this.getRankFile().getConfig().getInt("ranks." + rank.getKey() + ".power") != rank.getValue().getPower()) {
						this.getRankFile().getConfig().set("ranks." + rank.getKey() + ".power", rank.getValue().getPower());
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
		this.shutdown = true;
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
	}

	public void setupDatabase() {
		if (this.connection != null) {
			this.getMySQL().createPlayerManagerTableAsync();
			return;
		}
		Core.API.getLogger().info("WARNING enter valid database information (" + this.getHikariPath() + ") \n You will not be able to access many features");
	}

	public void setupHikariCP() {
		try {
			final HikariConfig config = new HikariConfig(this.getHikariPath());
			final HikariDataSource ds = new HikariDataSource(config);
			this.dataSource = ds;
			final String passwd = (config.getDataSourceProperties().getProperty("password") == null) ? "" : config.getDataSourceProperties().getProperty("password");
			BukkitDB.createHikariDatabase(this, config.getDataSourceProperties().getProperty("user"), passwd, config.getDataSourceProperties().getProperty("databaseName"), config.getDataSourceProperties().getProperty("serverName") + ":" + config.getDataSourceProperties().getProperty("portNumber"));
			this.connection = ds.getConnection();
		}
		catch (SQLException e) {
			Core.API.getLogger().info("Error could not connect to SQL database.");
			e.printStackTrace();
		}
		Core.API.getLogger().info("Successfully connected to the SQL database.");
	}
}

package akyto.core.handler.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

import akyto.core.disguise.DisguiseEntry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.google.common.collect.Lists;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.rank.RankEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.database.DatabaseType;
import com.google.common.collect.Maps;

public class ProfileManager {
	
	private final Core main;
	@Getter
    private final List<UUID> frozed = Lists.newArrayList();
    @Getter
    private final ConcurrentMap<UUID, Profile> profiles = Maps.newConcurrentMap();
    @Getter
    private final HashMap<UUID, PermissionAttachment> permissible = Maps.newHashMap();
	@Getter
	private final HashMap<UUID, DisguiseEntry> disguised = Maps.newHashMap();
	@Getter
	private final HashMap<String, String> realNameInDisguised = Maps.newHashMap();

    public ProfileManager(final Core main) {
		this.main = main;
		Core.API.getBypassCpsCap().addAll(Core.API.getConfig().getStringList("autoclicker.bypass"));
		Core.API.getWhitelisted().addAll(Core.API.getConfig().getStringList("whitelist.allowed"));
		Core.API.getBlacklistWhitelist().addAll(Core.API.getConfig().getStringList("whitelist.blacklist"));
		final long timeUnit = System.currentTimeMillis();
		if (main.getDatabaseType().equals(DatabaseType.FLAT_FILES)) {
			final File dir = new File(this.main.getDataFolder() + "/players/");
			File[] files = dir.listFiles();
			if (dir.exists()) {
				for (File file : files) {
					YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
					final String str = file.getName().replace(".yml", "");
					this.profiles.put(UUID.fromString(str), new Profile(UUID.fromString(str), configFile.getString("rank")));
				}	
			}	
		}
		long endTime = System.currentTimeMillis();
		System.out.println("[Profiles] Loaded in " + (endTime - timeUnit) + "ms!");
	}
	
	public void createProfile(final UUID uuid) {
		if (!this.profiles.containsKey(uuid)) {
			this.profiles.put(uuid, new Profile(uuid, "default"));
		}
		if (!this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
			this.registerPermissions(uuid);
		}
		final RankEntry rank = this.main.getManagerHandler().getProfileManager().getRank(uuid);
		final Player p = Bukkit.getPlayer(uuid);
		if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				final String perm = this.main.getLoaderHandler().getPermission().getStaffAnnounce();
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.hasPermission(perm) && !rank.equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && p.hasPermission(perm)) {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce()
								.replace("%rank%", rank.getPrefix())
								.replace("%rankColor%", CoreUtils.translate(rank.getColor()))
								.replace("%player%", p.getName())
								.replace("%type%", "join"));
					}
				});	
			}	
		}
        if (this.main.getLoaderHandler().getSettings().isNamemcCheck()) {
			CoreUtils.checkNameMCLikeAsync(main, uuid);
		}
		final String name = p.getName().substring(0, Math.min(Bukkit.getPlayer(uuid).getName().length(), 14));
		Bukkit.getPlayer(uuid).setPlayerListName(CoreUtils.translate(rank.getColor()) + name);
	}
	
	public void registerPermissions(final UUID uuid) {
		PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(this.main);
		this.getPermissible().put(uuid, attachment);
		this.getRank(uuid).getPermissions().forEach(perm -> attachment.setPermission(perm, true));
	}
	
	public RankEntry getRank(final UUID uuid) {
		return this.main.getManagerHandler().getRankManager().getRanks().get(
				this.getProfiles().get(uuid).getRank()
		);
	}
}

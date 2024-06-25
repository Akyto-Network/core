package akyto.core.handler.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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

    public ProfileManager(final Core main) {
		this.main = main;
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
		if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(uuid).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && Bukkit.getPlayer(uuid).hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce().replace("%rank%", this.main.getManagerHandler().getProfileManager().getRank(uuid).getPrefix()).replace("%rankColor%", CoreUtils.translate(this.main.getManagerHandler().getProfileManager().getRank(uuid).getColor())).replace("%player%", Bukkit.getPlayer(uuid).getName()).replace("%type%", "join"));
					}
				});	
			}	
		}
        if (this.main.getLoaderHandler().getSettings().isNamemcCheck()) {
			CompletableFuture<Boolean> future = CoreUtils.checkNameMCLikeAsync(main, uuid);
			future.thenAccept(result -> {
				this.profiles.get(uuid).setLikeNameMC(result);
				Bukkit.getPlayer(uuid).sendMessage(result ? this.main.getLoaderHandler().getMessage().getNameMCLike() : this.main.getLoaderHandler().getMessage().getNameMCUnlike());	
			});      
		}
		if (!this.main.getDatabaseType().equals(DatabaseType.MYSQL)){
			Bukkit.getPlayer(uuid).setPlayerListName(CoreUtils.translate(this.getRank(uuid).getColor()) + Bukkit.getPlayer(uuid).getName().substring(0, Math.min(Bukkit.getPlayer(uuid).getName().length(), 15)));
		}
	}
	
	public void registerPermissions(final UUID uuid) {
		PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(this.main);
		this.getPermissible().put(uuid, attachment);
		this.getRank(uuid).getPermissions().forEach(perm -> attachment.setPermission(perm, true));
	}
	
	public RankEntry getRank(final UUID uuid) {
		return this.main.getManagerHandler().getRankManager().getRanks().get(this.getProfiles().get(uuid).getRank());
	}
}
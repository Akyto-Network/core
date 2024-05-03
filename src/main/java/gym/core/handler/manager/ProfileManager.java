package gym.core.handler.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.google.common.collect.Lists;

import co.aikar.idb.DB;
import gym.core.Core;
import gym.core.profile.Profile;
import gym.core.rank.RankEntry;
import gym.core.utils.Utils;
import gym.core.utils.database.DatabaseType;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.util.com.google.common.collect.Maps;

public class ProfileManager {
	
	private Core main;
	private List<UUID> frozed = Lists.newArrayList();
	public List<UUID> getFrozed() { return frozed; }
	private ConcurrentMap<UUID, Profile> profiles = Maps.newConcurrentMap();
	public ConcurrentMap<UUID, Profile> getProfiles() { return profiles; }
	private HashMap<UUID, PermissionAttachment> permissible = Maps.newHashMap();
	public HashMap<UUID, PermissionAttachment> getPermissible() { return permissible; }
	
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
		System.out.println("[Profiles] Loaded in " + String.valueOf(endTime - timeUnit) + "ms!");
	}
	
	public void createProfile(final UUID uuid) {
		if (!this.profiles.containsKey(uuid)) {
			this.profiles.put(uuid, new Profile(uuid, "default"));
		}
		if (this.main.getDatabaseType().equals(DatabaseType.MYSQL)) {
			this.update(uuid);
		}
		this.registerPermissions(uuid);
		if (this.main.getLoaderHandler().getSettings().isStaffNotifications()) {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce()) && !this.main.getManagerHandler().getProfileManager().getRank(uuid).equals(this.main.getManagerHandler().getRankManager().getRanks().get("default")) && Bukkit.getPlayer(uuid).hasPermission(this.main.getLoaderHandler().getPermission().getStaffAnnounce())) {
						player.sendMessage(this.main.getLoaderHandler().getMessage().getStaffAnnounce().replace("%rank%", this.main.getManagerHandler().getProfileManager().getRank(uuid).getPrefix()).replace("%rankColor%", Utils.translate(this.main.getManagerHandler().getProfileManager().getRank(uuid).getColor())).replace("%player%", Bukkit.getPlayer(uuid).getName()).replace("%type%", "join"));
					}
				});	
			}	
		};
		if (this.main.getLoaderHandler().getSettings().isNamemcCheck()) {
			CompletableFuture<Boolean> future = Utils.checkNameMCLikeAsync(main, uuid);
			future.thenAccept(result -> {
				this.profiles.get(uuid).setLikeNameMC(result);
				Bukkit.getPlayer(uuid).sendMessage(result ? this.main.getLoaderHandler().getMessage().getNameMCLike() : this.main.getLoaderHandler().getMessage().getNameMCUnlike());	
			});      
		}
		Bukkit.getPlayer(uuid).setPlayerListName(Utils.translate(this.getRank(uuid).getColor()) + Bukkit.getPlayer(uuid).getName().substring(0, Math.min(Bukkit.getPlayer(uuid).getName().length(), 15)));
	}
	
	public void registerPermissions(final UUID uuid) {
		PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(this.main);
		this.getPermissible().put(uuid, attachment);
		this.getRank(uuid).getPermissions().forEach(perm -> attachment.setPermission(perm, true));
	}
	
	public RankEntry getRank(final UUID uuid) {
		return this.main.getManagerHandler().getRankManager().getRanks().get(this.getProfiles().get(uuid).getRank());
	}
	
	public void update(final UUID uuid) {
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
            this.loadAsync(uuid);
	    } catch (InterruptedException | ExecutionException e) {
	        e.printStackTrace();
	    }
	}
	
	public void loadAsync(final UUID uuid) {
	    final Player player = Bukkit.getPlayer(uuid);
	    final Profile data = this.getProfiles().get(uuid);
        String playerName = player.getName();
        CompletableFuture<String> rankFuture = DB.getFirstRowAsync("SELECT rank FROM coredata WHERE name=?", playerName)
                .thenApply(row -> row.getString("rank"));
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(rankFuture);
        allOfFuture.join();
        data.setRank(rankFuture.join());
        this.registerPermissions(uuid);
	}
	
	public void exitAsync(final UUID uuid) {
		final Profile data = this.getProfiles().get(uuid);
		if (data != null) {
			DB.executeUpdateAsync("UPDATE coredata SET rank=? WHERE name=?", data.getRank(), Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getName() : Bukkit.getOfflinePlayer(uuid).getName()).join();
		}
	}

}

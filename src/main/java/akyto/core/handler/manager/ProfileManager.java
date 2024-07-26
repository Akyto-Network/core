package akyto.core.handler.manager;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

import akyto.core.disguise.DisguiseEntry;
import akyto.core.settings.NormalSettings;
import akyto.core.settings.SpectateSettings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
	@Getter
	private final HashMap<UUID, UUID> friendsRequest = Maps.newHashMap();

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
					this.profiles.put(UUID.fromString(str), new Profile(UUID.fromString(str), configFile.getString("rank"), "none"));
				}	
			}	
		}
		long endTime = System.currentTimeMillis();
		System.out.println("[Profiles] Loaded in " + (endTime - timeUnit) + "ms!");
	}
	
	public void createProfile(final UUID uuid) {
		if (!this.profiles.containsKey(uuid)) {
			this.profiles.put(uuid, new Profile(uuid, "default", "none"));
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
		final List<String> perms = Lists.newArrayList();
		perms.addAll(this.getProfiles().get(uuid).getPermissions());
		perms.addAll(this.getRank(uuid).getPermissions());
		perms.forEach(perm -> attachment.setPermission(perm, true));
	}
	
	public RankEntry getRank(final UUID uuid) {
		return this.main.getManagerHandler().getRankManager().getRanks().get(
				this.getProfiles().get(uuid).getRank()
		);
	}

	public Profile getProfile(final UUID uuid) {
		if (this.getProfiles().get(uuid) == null) {
			System.out.println("Profile " + uuid + " not found.");
			return null;
		}
		return this.getProfiles().get(uuid);
	}

	// SETTINGS CREDITS TO TETELIE*

	public void refreshSettingLore(Inventory settingInv, final UUID uuid, int slot, int setting, boolean normal) {
		ItemStack item = settingInv.getItem(slot);
		ItemMeta meta = item.getItemMeta();
		meta.getLore().clear();
		List<String> lore = new ArrayList<>();
		lore.add("§7§m----------------------");
		lore.addAll(Arrays.asList(getSettingLore(uuid, setting, normal)));
		lore.add("§7§m----------------------");
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public void refreshSettingsLoreInv(Inventory settingsInv, final UUID uuid, boolean normal) {
		int setting = 0;
		if (!normal) {
			for (SpectateSettings settings : SpectateSettings.all) {
				refreshSettingLore(settingsInv, uuid, settings.slot(), setting, false);
				setting++;
			}
			return;
		}
		for (NormalSettings settings : NormalSettings.all) {
			refreshSettingLore(settingsInv, uuid, settings.slot(), setting, true);
			setting++;
		}
	}

	private String[] getSettingLore(UUID uuid, int id, boolean normal) {
		final Profile profile = this.getProfiles().get(uuid);
		int value = profile.getSettings()[id];
		if (!normal) {
			value = profile.getSettings()[id == 0 ? 7 : 8];
			SpectateSettings setting = SpectateSettings.all[id];
			String[] lore = setting.values();
			String newLore = ChatColor.RESET + setting.values()[value];
			lore[value] = ChatColor.GRAY + " » " + newLore;
			return lore;
		}
		NormalSettings setting = NormalSettings.all[id];
		String[] lore = setting.values();
		String newLore = ChatColor.RESET + setting.values()[value];
		lore[value] = ChatColor.GRAY + " » " + newLore;
		return lore;
	}

	public void changeSettings(int setting, Player player, boolean normal) {
		final Profile profile = this.getProfiles().get(player.getUniqueId());
		int currentValue = profile.getSettings()[setting];
		if (!normal) {
			currentValue = profile.getSettings()[setting == 0 ? 7 : 8];
			int maxValue = SpectateSettings.all[setting].values().length;
			int newValue = (currentValue + 1) % maxValue;
			SpectateSettings.all[setting].change(player, newValue);
			return;
		}
		int maxValue = NormalSettings.all[setting].values().length;
		int newValue = (currentValue + 1) % maxValue;
		NormalSettings.all[setting].change(player, newValue);
	}
}

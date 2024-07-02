package akyto.core.punishment.file;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import akyto.core.punishment.cache.BlacklistEntry;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import akyto.core.Core;
import akyto.core.punishment.cache.BanEntry;
import akyto.core.punishment.cache.MuteEntry;
import akyto.core.punishment.cache.WarnEntry;

public class PunishmentFile {
	
	private final Core main;
	@Getter
    private YamlConfiguration config;
    @Getter
    private File file;

    public PunishmentFile(final Core main) {
		this.main = main;
		this.generate();
	}

	private void generate() {
		final long startTime = System.currentTimeMillis();
		file = new File(this.main.getDataFolder(), "punishments.yml");
		if (!file.exists()) {            
			this.main.saveResource("punishments.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getKeys(true).size() > 2) {
			if(getConfig().getConfigurationSection("banned") != null) {
				for (String str : getConfig().getConfigurationSection("banned").getKeys(false)) {
					this.main.getManagerHandler().getPunishmentManager().getBanned().put(UUID.fromString(str), new BanEntry(getConfig().getConfigurationSection("banned").getString(str + ".expires"), getConfig().getConfigurationSection("banned").getString(str + ".reason"),getConfig().getConfigurationSection("banned").getString(str + ".judge")));
				}		
			}
			if(getConfig().getConfigurationSection("blacklisted") != null) {
				for (String str : getConfig().getConfigurationSection("blacklisted").getKeys(false)) {
					this.main.getManagerHandler().getPunishmentManager().getBlacklisted().put(UUID.fromString(str), new BlacklistEntry(getConfig().getConfigurationSection("blacklisted").getString(str + ".ip"), getConfig().getConfigurationSection("blacklisted").getString(str + ".reason"),getConfig().getConfigurationSection("blacklisted").getString(str + ".judge")));
				}
			}
			if (getConfig().getConfigurationSection("muted") != null) {
				for (String str : getConfig().getConfigurationSection("muted").getKeys(false)) {
					this.main.getManagerHandler().getPunishmentManager().getMuted().put(UUID.fromString(str), new MuteEntry(getConfig().getConfigurationSection("muted").getString(str + ".expires"), getConfig().getConfigurationSection("muted").getString(str + ".reason"),getConfig().getConfigurationSection("muted").getString(str + ".judge")));
				}	
			}
			if (getConfig().getConfigurationSection("warned") != null) {
				for (String str : getConfig().getConfigurationSection("warned").getKeys(false)) {
					this.main.getManagerHandler().getPunishmentManager().getWarned().put(UUID.fromString(str), new WarnEntry(getConfig().getConfigurationSection("warned").getInt(str + ".counter"),getConfig().getConfigurationSection("warned").getStringList(str + ".by")));
				}	
			}
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Punishments: Succesfully loaded in " + (endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

package akyto.core.disguise.file;

import akyto.core.Core;
import akyto.core.disguise.DisguiseEntry;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DisguiseFile {

	private final Core main;
	@Getter
    private YamlConfiguration config;
    @Getter
    private File file;

    public DisguiseFile(final Core main) {
		this.main = main;
		this.generate();
	}

	private void generate() {
		final long startTime = System.currentTimeMillis();
		file = new File(this.main.getDataFolder(), "api/disguise.yml");
		if (!file.exists()) {            
			this.main.saveResource("api/disguise.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getKeys(true).size() > 2) {
			for (String str : getConfig().getConfigurationSection("disguise").getKeys(false)) {
				DisguiseEntry disguise = new DisguiseEntry(str,
						getConfig().getConfigurationSection("disguise").getString(str + ".data"),
						getConfig().getConfigurationSection("disguise").getString(str + ".signature")
				);
				this.main.getManagerHandler().getServerManager().getDisguise().put(str, disguise);
			}	
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Disguised: Succesfully loaded in " + (endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

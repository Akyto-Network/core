package akyto.core.tag.file;

import akyto.core.Core;
import akyto.core.tag.TagEntry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TagFile {
	
	private final Core main;
	@Getter
    private YamlConfiguration config;
    @Getter
    private File file;

    public TagFile(final Core main) {
		this.main = main;
		this.generate();
	}

	private void generate() {
		final long startTime = System.currentTimeMillis();
		file = new File(this.main.getDataFolder(), "api/tags.yml");
		if (!file.exists()) {            
			this.main.saveResource("api/tags.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getKeys(true).size() > 2) {
			for (String str : getConfig().getConfigurationSection("common").getKeys(false)) {
				TagEntry tag = new TagEntry(getConfig().getConfigurationSection("common").getString(str + ".prefix"), getConfig().getConfigurationSection("common").getString(str + ".permissions"), getConfig().getConfigurationSection("common").getInt(str + ".tokens-price"), Material.valueOf(getConfig().getConfigurationSection("common").getString(str + ".icon")));
				this.main.getManagerHandler().getTagManager().getTags().put(str, tag);
			}	
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Tags: Succesfully loaded in " + (endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

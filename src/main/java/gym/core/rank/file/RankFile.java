package gym.core.rank.file;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import gym.core.Core;
import gym.core.rank.RankEntry;

public class RankFile {
	
	private Core main;
	private YamlConfiguration config;
	public YamlConfiguration getConfig() { return config; }
	private File file;
	public File getFile() { return file; }
	
	public RankFile(final Core main) {
		this.main = main;
		this.generate();
	}

	private void generate() {
		final long startTime = System.currentTimeMillis();
		file = new File(this.main.getDataFolder(), "ranks.yml");
		if (!file.exists()) {            
			this.main.saveResource("ranks.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getKeys(true).size() > 2) {
			for (String str : getConfig().getConfigurationSection("ranks").getKeys(false)) {
				final String spacer = getConfig().getConfigurationSection("ranks").getString(str + ".spaceBetweenPrefixAndColor");
				this.main.getManagerHandler().getRankManager().getRanks().put(str, new RankEntry(getConfig().getConfigurationSection("ranks").getString(str + ".prefix"), getConfig().getConfigurationSection("ranks").getString(str + ".color"), Boolean.valueOf(spacer), getConfig().getConfigurationSection("ranks").getStringList(str + ".permissions")));
			}	
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Rank: Succesfully loaded in " + String.valueOf(endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

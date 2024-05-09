package gym.core.rank.file;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import gym.core.Core;
import gym.core.rank.RankEntry;
import gym.core.rank.RankType;

public class RankFile {
	
	private final Core main;
	@Getter
    private YamlConfiguration config;
    @Getter
    private File file;

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
				this.main.getManagerHandler().getRankManager().getRanks().put(str, new RankEntry(getConfig().getConfigurationSection("ranks").getString(str + ".prefix"), getConfig().getConfigurationSection("ranks").getString(str + ".color"), Boolean.valueOf(spacer), getConfig().getConfigurationSection("ranks").getStringList(str + ".permissions"), RankType.valueOf(getConfig().getConfigurationSection("ranks").getString(str + ".type"))));
			}	
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Rank: Succesfully loaded in " + (endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

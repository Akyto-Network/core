package akyto.core.utils.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public class Config {
    private final FileConfiguration config;
    private final File configFile;
    protected boolean wasCreated;
    
    public Config(final String name, final JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        if (!this.configFile.exists()) {
            try {
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();
                this.wasCreated = true;
            }
            catch (IOException e) {
                plugin.getLogger().severe("Failed to create config file " + this.configFile.getPath() + "\n" + e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public void save() {
        try {
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

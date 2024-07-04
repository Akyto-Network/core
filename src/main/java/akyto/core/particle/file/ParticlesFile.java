package akyto.core.particle.file;

import akyto.core.Core;
import akyto.core.particle.ParticleEntry;
import akyto.core.utils.CoreUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ParticlesFile {

	private final Core main;
	@Getter
    private YamlConfiguration config;
    @Getter
    private File file;

    public ParticlesFile(final Core main) {
		this.main = main;
		this.generate();
	}

	private void generate() {
		final long startTime = System.currentTimeMillis();
		file = new File(this.main.getDataFolder(), "particles.yml");
		if (!file.exists()) {            
			this.main.saveResource("particles.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getKeys(true).size() > 2) {
			for (String str : getConfig().getConfigurationSection("particles").getKeys(false)) {
				ParticleEntry particle = new ParticleEntry(
						str,
						EnumParticle.valueOf(getConfig().getConfigurationSection("particles").getString(str + ".type")),
						getConfig().getConfigurationSection("particles").getFloat(str + ".xOffSet"),
						getConfig().getConfigurationSection("particles").getFloat(str + ".yOffSet"),
						getConfig().getConfigurationSection("particles").getFloat(str + ".zOffSet"),
						getConfig().getConfigurationSection("particles").getFloat(str + ".speed"),
						getConfig().getConfigurationSection("particles").getInt(str + ".amount"),
						CoreUtils.translate(getConfig().getConfigurationSection("particles").getString(str + ".inventory.name")),
						getConfig().getConfigurationSection("particles").getStringList(str + ".inventory.lore"),
						Material.valueOf(getConfig().getConfigurationSection("particles").getString(str + ".inventory.icon")),
						getConfig().getConfigurationSection("particles").getString(str + ".permissions")
				);
				this.main.getParticles().add(particle);
			}	
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("[CORE] Particles: Succesfully loaded in " + (endTime - startTime) + "ms!");
	}

	public void save() {
		try {
			getConfig().save(getFile()); 
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
}

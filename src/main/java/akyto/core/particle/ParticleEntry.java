package akyto.core.particle;

import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.List;

@Getter
public class ParticleEntry {

    private final Effect particle;
    private final float xOffSet;
    private final float yOffSet;
    private final float zOffSet;
    private final float speed;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final Material icon;
    private final String section;
    private final String permission;

    public ParticleEntry(String section, Effect particle, float xOffSet, float yOffSet, float zOffSet, float speed, int amount, final String name, final List<String> lore, final Material icon, final String permission) {
        this.section = section;
        this.particle = particle;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
        this.zOffSet = zOffSet;
        this.speed = speed;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.icon = icon;
        this.permission = permission;
    }
}

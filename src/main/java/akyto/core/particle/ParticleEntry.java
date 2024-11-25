package akyto.core.particle;

import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Material;

import java.util.List;

@Getter
public class ParticleEntry {

    private Effect particle;
    private float xOffSet;
    private float yOffSet;
    private float zOffSet;
    private float speed;
    private int amount;
    private String name;
    private List<String> lore;
    private Material icon;
    private String section;
    private String permission;

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

package akyto.core.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Effects {

    public abstract int id();

    public abstract void invoke(Player player, Location location);

}
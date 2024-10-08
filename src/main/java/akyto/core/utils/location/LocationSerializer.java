package akyto.core.utils.location;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.*;

@Getter @Setter
public class LocationSerializer
{
    private final long timestamp;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public LocationSerializer(final double x, final double y, final double z) {
        this(x, y, z, 0.0f, 0.0f);
    }
    
    public LocationSerializer(final String world, final double x, final double y, final double z) {
        this(world, x, y, z, 0.0f, 0.0f);
    }
    
    public LocationSerializer(final double x, final double y, final double z, final float yaw, final float pitch) {
        this("world", x, y, z, yaw, pitch);
    }
    
    public static LocationSerializer fromBukkitLocation(final Location location) {
        return new LocationSerializer(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public static LocationSerializer stringToLocation(final String string) {
        final String[] split = string.split(", ");
        final double x = Double.parseDouble(split[0]);
        final double y = Double.parseDouble(split[1]);
        final double z = Double.parseDouble(split[2]);
        final LocationSerializer customLocation = new LocationSerializer(x, y, z);
        if (split.length == 4) {
            customLocation.setWorld(split[3]);
        }
        else if (split.length >= 5) {
            customLocation.setYaw(Float.parseFloat(split[3]));
            customLocation.setPitch(Float.parseFloat(split[4]));
            if (split.length >= 6) {
                customLocation.setWorld(split[5]);
            }
        }
        return customLocation;
    }
    
    public static String locationToString(final LocationSerializer loc) {
        final StringJoiner joiner = new StringJoiner(", ");
        joiner.add(Double.toString(loc.getX()));
        joiner.add(Double.toString(loc.getY()));
        joiner.add(Double.toString(loc.getZ()));
        if (loc.getYaw() != 0.0f || loc.getPitch() != 0.0f) {
            joiner.add(Float.toString(loc.getYaw()));
            joiner.add(Float.toString(loc.getPitch()));
        }
        if (!loc.getWorld().equals("world"))
            joiner.add(loc.getWorld());
        return joiner.toString();
    }
    
    public Location toBukkitLocation() {
        return new Location(this.toBukkitWorld(), this.x, this.y+1.5d, this.z, this.yaw, this.pitch);
    }
    
    public double getGroundDistanceTo(final LocationSerializer location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.z - location.z, 2.0));
    }
    
    public double getDistanceTo(final LocationSerializer location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.y - location.y, 2.0) + Math.pow(this.z - location.z, 2.0));
    }
    
    public World toBukkitWorld() {
        if (this.world == null) {
            return Bukkit.getServer().getWorlds().get(0);
        }
        return Bukkit.getServer().getWorld(this.world);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof LocationSerializer)) {
            return false;
        }
        final LocationSerializer location = (LocationSerializer)obj;
        return location.x == this.x && location.y == this.y && location.z == this.z && location.pitch == this.pitch && location.yaw == this.yaw;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("x", this.x).append("y", this.y).append("z", this.z).append("yaw", this.yaw).append("pitch", this.pitch).append("world", this.world).append("timestamp", this.timestamp).toString();
    }

    public LocationSerializer(final String world, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.timestamp = System.currentTimeMillis();
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}

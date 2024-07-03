package akyto.core.utils.particle;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleUtils {

    private PacketPlayOutWorldParticles packet;

    public ParticleUtils(EnumParticle type, Location loc, float xOffset, float yOffset, float zOffset, float speed, int count) {
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        float z = (float) loc.getZ();

        this.packet = new PacketPlayOutWorldParticles(type,
                true,
                x,
                y,
                z,
                xOffset,
                yOffset,
                zOffset,
                speed,
                count,
                null);
    }

    public void sendToAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void sendToPlayer(Player p) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

}
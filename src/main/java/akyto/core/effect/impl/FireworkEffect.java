package akyto.core.effect.impl;

import akyto.core.effect.Effects;
import akyto.core.utils.particle.ParticleUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FireworkEffect extends Effects {

    @Override
    public int id() {
        return 2;
    }

    @Override
    public void invoke(Player player, Location location) {
        ParticleUtils particleApi = new ParticleUtils(EnumParticle.FIREWORKS_SPARK, location, 0.1f, 0.1f, 0.1f, 0.08f, 40);
        player.playSound(location, Sound.FIREWORK_LARGE_BLAST2, 1f, 1f);
        particleApi.sendToPlayer(player);
    }
}


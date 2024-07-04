package akyto.core.handler.command;

import akyto.core.particle.ParticleEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.particle.ParticleUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ParticleCommand {

    @Command(name = "particle", aliases= {"part"}, inGameOnly = true)
    public void giveaway(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        if (arg.getArgs().length != 1) {
            sender.sendMessage(ChatColor.RED + "/particle <particle_name>");
            return;
        }
        if (CoreUtils.getParticleBySection(arg.getArgs(0)) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid particle");
            return;
        }
        ParticleEntry entry = CoreUtils.getParticleBySection(arg.getArgs(0));
        ParticleUtils utils = new ParticleUtils(entry.getParticle(), sender.getLocation(), entry.getXOffSet(), entry.getYOffSet(), entry.getZOffSet(), entry.getSpeed(), entry.getAmount());
        utils.sendToPlayer(sender);
    }

}

package gym.core.handler.command;

import akyto.spigot.util.ItemBuilder;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand {
    @Command(name = "admintest", permission = "akyto.admin")
    public void testCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        if (arg.isPlayer()) {
            Player player = arg.getPlayer();
            player.getInventory().addItem(new ItemBuilder(Material.POTION).toItemStack());
        }
    }
}

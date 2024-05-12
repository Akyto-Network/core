package gym.core.handler.command;

import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadCommand {

    @Command(
            name = "head",
            permission = "akyto.head",
            description = "Place the item in your hand on your head",
            usage = "Usage: /head",
            inGameOnly = true
    )
    public void headCommand(final CommandArgs arg) {
        Player sender = arg.getPlayer();

        if (arg.length() != 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /head");
            return;
        }

        ItemStack hand = sender.getItemInHand();

        if (hand == null || hand.getType().equals(Material.AIR)) {
            sender.sendMessage(ChatColor.RED + "You must be holding something!");
            return;
        }

        sender.getEquipment().setHelmet(hand.clone());
    }
}

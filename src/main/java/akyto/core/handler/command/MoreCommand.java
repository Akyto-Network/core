package akyto.core.handler.command;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand {

    @Command(
            name = "more",
            permission = "akyto.more",
            description = "Stack 64 items of what's in your hand",
            usage = "Usage: /more",
            inGameOnly = true
    )
    public void moreCommand(final CommandArgs arg) {
        Player sender = arg.getPlayer();

        if (arg.length() != 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /more");
            return;
        }

        ItemStack hand = sender.getItemInHand();

        if (hand == null || hand.getType().equals(Material.AIR)) {
            sender.sendMessage(ChatColor.RED + "You must be holding something!");
            return;
        }

        ItemStack more = hand.clone();
        more.setAmount(64);

        sender.setItemInHand(more);
    }
}

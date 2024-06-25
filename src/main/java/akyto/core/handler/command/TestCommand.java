package akyto.core.handler.command;

import akyto.spigot.util.ItemBuilder;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestCommand {
    @Command(name = "admintest", permission = "akyto.admin")
    public void testCommand(final CommandArgs arg) {
        final CommandSender sender = arg.getSender();
        if (arg.isPlayer()) {
            Player player = arg.getPlayer();
            ItemStack is = new ItemBuilder(Material.POTION, 1, (short) 16421)
                    .hideEverything()
                    .setLore(List.of())
                    .toItemStack();
            // Need also packet modifying
            net.minecraft.server.v1_8_R3.ItemStack nmsIs = CraftItemStack.asNMSCopy(is.clone());
            NBTTagCompound tag;
            if(!nmsIs.hasTag()) {
                tag = new NBTTagCompound();
                nmsIs.setTag(tag);
            }
            else {
                tag = nmsIs.getTag();
            }
            NBTTagList am = new NBTTagList();
            tag.set("AttributeModifiers", am);
            nmsIs.setTag(tag);

            player.sendMessage(((NBTTagList)tag.get("AttributeModifiers")).toString());
            is = CraftItemStack.asCraftMirror(nmsIs);

            player.getInventory().addItem(is);
            player.sendMessage(nmsIs.toString());
            player.sendMessage(String.valueOf(nmsIs.getTag()));
            List<String> lore = is.getItemMeta().getLore();
            if (lore == null || lore.isEmpty())
                player.sendMessage("Lore is empty");
            else {
                for (int i = 0; i < lore.size(); i++) {
                    player.sendMessage(i + " " + lore.get(i));
                }
            }
        }
    }
}

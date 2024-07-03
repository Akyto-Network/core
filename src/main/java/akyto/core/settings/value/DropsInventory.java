package akyto.core.settings.value;

import akyto.core.Core;
import akyto.core.settings.NormalSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DropsInventory extends NormalSettings {
    @Override
    public int slot() {
        return 5;
    }

    @Override
    public String[] values() {
        return new String[] {
                ChatColor.GREEN + "clear",
                ChatColor.YELLOW + "only armor",
                ChatColor.GOLD + "all"
        };
    }

    @Override
    public void change(Player player, int value) {
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId()).getSettings()[5] = value;
    }
}

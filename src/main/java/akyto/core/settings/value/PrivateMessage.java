package akyto.core.settings.value;

import akyto.core.Core;
import akyto.core.settings.NormalSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrivateMessage extends NormalSettings {
    @Override
    public int slot() {
        return 4;
    }

    @Override
    public String[] values() {
        return new String[]{
                ChatColor.GREEN + "enable",
                ChatColor.RED + "disable"
        };
    }

    @Override
    public void change(Player player, int value) {
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId()).getSettings()[4] = value;
    }
}

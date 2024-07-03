package akyto.core.settings.value;

import akyto.core.Core;
import akyto.core.settings.NormalSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DeathEffect extends NormalSettings {

    @Override
    public int slot() {
        return 8;
    }

    @Override
    public String[] values() {
        return new String[] {
                ChatColor.RED + "disable",
                ChatColor.YELLOW + "Smoke",
                ChatColor.DARK_RED + "Explode",
                ChatColor.GOLD + "Fireworks"
        };
    }

    @Override
    public void change(Player player, int value) {
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId()).getSettings()[9] = value;
    }
}

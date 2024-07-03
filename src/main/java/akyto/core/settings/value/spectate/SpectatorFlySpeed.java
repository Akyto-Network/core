package akyto.core.settings.value.spectate;

import akyto.core.Core;
import akyto.core.settings.SpectateSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectatorFlySpeed extends SpectateSettings {
    @Override
    public int slot() {
        return 0;
    }

    @Override
    public String[] values() {
        return new String[] {
                ChatColor.LIGHT_PURPLE + "x2.5",
                ChatColor.AQUA + "x1.0"
        };
    }

    @Override
    public void change(Player player, int value) {
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId()).getSettings()[7] = value;
    }
}

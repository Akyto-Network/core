package akyto.core.settings.value;

import akyto.core.Core;
import akyto.core.settings.NormalSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Time extends NormalSettings {

    @Override
    public int slot() {
        return 2;
    }

    @Override
    public String[] values() {
        return new String[]
                {
                        ChatColor.DARK_AQUA + "sunrise",
                        ChatColor.GOLD + "day",
                        ChatColor.YELLOW + "sunset",
                        ChatColor.BLUE + "night"
                };
    }

    @Override
    public void change(Player player, int value) {
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(player.getUniqueId()).getSettings()[2] = value;
        switch (value)
        {
            case 0:
                player.setPlayerTime(0, false);
                break;
            case 1:
                player.setPlayerTime(6250, false);
                break;
            case 2:
                player.setPlayerTime(13000, false);
                break;
            case 3:
                player.setPlayerTime(14000, false);
                break;
        }
    }
}

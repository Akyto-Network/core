package akyto.core.settings;

import akyto.core.settings.value.spectate.SpectatorCanSeeOtherSpecs;
import akyto.core.settings.value.spectate.SpectatorFlySpeed;
import org.bukkit.entity.Player;

public abstract class SpectateSettings {

    public static SpectateSettings[] all = new SpectateSettings[]{
            new SpectatorFlySpeed(),
            new SpectatorCanSeeOtherSpecs()
    };

    public abstract int slot();
    public abstract String[] values();
    public abstract void change(Player player, int value);

    public static int getSettingsBySlot(int slot)
    {
        for (SpectateSettings setting : all)
        {
            if (setting.slot() == slot) return setting.slot();
        }
        return 0;
    }

}

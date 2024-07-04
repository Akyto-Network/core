package akyto.core.settings;

import akyto.core.settings.value.*;
import org.bukkit.entity.Player;

public abstract class NormalSettings {

    public static NormalSettings[] all = new NormalSettings[]{
            new Scoreboard(),
            new DuelRequest(),
            new Time(),
            new AllowSpectate(),
            new PrivateMessage(),
            new DropsInventory(),
            new ClearInventory()
    };

    public abstract int slot();
    public abstract String[] values();
    public abstract void change(Player player, int value);


    public static int getSettingsBySlot(int slot)
    {
        int id = 0;
        for(NormalSettings setting : all)
        {
            if(setting.slot() == slot) {
                return setting.slot();
            }
        }
        return id;
    }

}

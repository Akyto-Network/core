package akyto.core.handler.command;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class GiveawayCommand {

    @Command(name = "giveaway", aliases= {"drops"}, inGameOnly = true)
    public void giveaway(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        //TODO: Make conditions with token system!
    }
}

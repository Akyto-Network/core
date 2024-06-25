package akyto.core.handler.command;

import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class TagsCommand {

    @Command(name = "tag", aliases= {"tags"}, inGameOnly = true)
    public void tagCommand(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
    }
}

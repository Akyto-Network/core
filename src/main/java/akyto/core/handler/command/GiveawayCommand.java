package akyto.core.handler.command;

import akyto.core.Core;
import akyto.core.giveaway.GiveawayType;
import akyto.core.profile.Profile;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class GiveawayCommand {

    @Command(name = "giveaway", aliases= {"drops"}, inGameOnly = true)
    public void giveaway(final CommandArgs arg) {
        final Player sender = arg.getPlayer();
        if (arg.getArgs().length < 1) {
            Core.API.getLoaderHandler().getMessage().getGiveawayHelp().forEach(sender::sendMessage);
            return;
        }
        if (arg.getArgs().length == 1) {
            if (arg.getArgs()[0].equalsIgnoreCase("rank")) {
                final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(arg.getPlayer().getUniqueId());
                if (profile.getTokens() < Core.API.getLoaderHandler().getSettings().getTokensPriceRank()) {
                    final int tokensMissed = Core.API.getLoaderHandler().getSettings().getTokensPriceRank() - profile.getTokens();
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokensMissing()
                            .replace("%tokens%", String.valueOf(tokensMissed))
                    );
                    return;
                }
                Core.API.getManagerHandler().getGiveawayManager().create(arg.getPlayer().getUniqueId(), GiveawayType.RANK);
            }
            else if (arg.getArgs()[0].equalsIgnoreCase("join")) {
                Core.API.getManagerHandler().getGiveawayManager().join(arg.getPlayer().getUniqueId());
            }
            else {
                Core.API.getLoaderHandler().getMessage().getGiveawayHelp().forEach(sender::sendMessage);
            }
        }
        else {
            Core.API.getLoaderHandler().getMessage().getGiveawayHelp().forEach(sender::sendMessage);
        }
    }
}

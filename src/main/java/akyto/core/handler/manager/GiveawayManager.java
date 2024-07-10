package akyto.core.handler.manager;

import akyto.core.Core;
import akyto.core.giveaway.Giveaway;
import akyto.core.giveaway.GiveawayType;
import akyto.core.runnable.GiveawayRunnable;
import akyto.core.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.UUID;

public class GiveawayManager {

    public void create(final UUID creator, final GiveawayType type) {
        if (!Core.API.getManagerHandler().getServerManager().getGiveaways().isEmpty()) {
            Bukkit.getPlayer(creator).sendMessage(ChatColor.RED + "A giveaway is already underway, so please wait for it to finish.");
            return;
        }
        Core.API.getManagerHandler().getServerManager().getGiveaways().add(new Giveaway(creator, type));
        final String text = Core.API.getLoaderHandler().getMessage().getGiveawayStartNotif()
                .replace("%player%", CoreUtils.getName(creator))
                .replace("%type%", type.toString().toLowerCase());

        final TextComponent textComponent = new TextComponent(text);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click here to join the giveway").create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/giveaway join"));
        Bukkit.getOnlinePlayers().forEach(players -> players.spigot().sendMessage(textComponent));
        final int tokenPrice = type.equals(GiveawayType.RANK) ? Core.API.getLoaderHandler().getSettings().getTokensPriceRank() : Core.API.getLoaderHandler().getSettings().getTokensPriceWhitelist();
        Core.API.getManagerHandler().getProfileManager().getProfiles().get(creator)
                .setTokens(Core.API.getManagerHandler().getProfileManager().getProfiles().get(creator).getTokens()-tokenPrice);
    }

    public void join(final UUID joiner) {
        final Giveaway giveaway = Core.API.getManagerHandler().getServerManager().getGiveaways().get(0);
        if (giveaway == null) {
            Bukkit.getPlayer(joiner).sendMessage(ChatColor.RED + "No giveaways!");
            return;
        }
        if (giveaway.getCreator().equals(joiner)) {
            Bukkit.getPlayer(joiner).sendMessage(ChatColor.RED + "You cannot join your giveaway.");
            return;
        }
        if (giveaway.getParticipants().contains(joiner)) {
            Bukkit.getPlayer(joiner).sendMessage(ChatColor.RED + "You are already in!");
            return;
        }
        if (giveaway.getParticipants().isEmpty()) {
            new GiveawayRunnable(giveaway.getCreator()).runTaskTimer(Core.API, 20L, 20L);
        }
        giveaway.getParticipants().add(joiner);
        Bukkit.getPlayer(joiner).sendMessage(Core.API.getLoaderHandler().getMessage().getGiveawayJoin()
                .replace("%player%", CoreUtils.getName(joiner))
        );
    }

    public UUID pick() {
        Collections.shuffle(Core.API.getManagerHandler().getServerManager().getGiveaways().get(0).getParticipants());
        return Core.API.getManagerHandler().getServerManager().getGiveaways().get(0).getParticipants().get(0);
    }
}

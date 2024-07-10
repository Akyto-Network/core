package akyto.core.runnable;

import akyto.core.Core;
import akyto.core.giveaway.GiveawayType;
import akyto.core.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GiveawayRunnable extends BukkitRunnable {

    private int idx = 120;
    private UUID creator;

    public GiveawayRunnable(final UUID creator) {
        this.creator = creator;
        this.run();
    }

    @Override
    public void run() {
        idx = idx-1;
        final String text = Core.API.getLoaderHandler().getMessage().getGiveawayStartNotif()
                .replace("%player%", CoreUtils.getName(creator))
                .replace("%type%", Core.API.getManagerHandler().getServerManager().getGiveaways().get(0).getType().toString().toLowerCase());
        final TextComponent textComponent = new TextComponent(text);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click here to join the giveway").create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/giveaway join"));
        if (idx == 60 || idx == 30 || idx == 15 || idx == 5) {
            Bukkit.getOnlinePlayers().forEach(players -> players.spigot().sendMessage(textComponent));
        }
        if (idx == 0) {
            final UUID winner = Core.API.getManagerHandler().getGiveawayManager().pick();
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.sendMessage(Core.API.getLoaderHandler().getMessage().getGiveawayWinner()
                        .replace("%winner%", CoreUtils.getName(winner))
                        .replace("%type%", Core.API.getManagerHandler().getServerManager().getGiveaways().get(0).getType().toString().toLowerCase())
                );
            });
            if (Core.API.getManagerHandler().getServerManager().getGiveaways().get(0).getType().equals(GiveawayType.RANK)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rank promote " + CoreUtils.getName(winner) + " " + Core.API.getLoaderHandler().getSettings().getRankGiveaway());
            }
            Core.API.getManagerHandler().getServerManager().getGiveaways().clear();
            cancel();
        }
    }
}
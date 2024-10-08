package akyto.core.handler.command.commons;

import akyto.core.Core;
import akyto.core.rank.RankEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ListCommand {

    private static String formatRank(Map.Entry<String, RankEntry> rank) {
        return CoreUtils.translate(rank.getValue().getColor()) + StringUtils.capitalize(rank.getKey());
    }

	@Command(name = "list")
    public void listCommand(final CommandArgs arg) {
    	final CommandSender sender = arg.getSender();
        StringBuilder builder = new StringBuilder();

        builder.append(
                Core.API.getManagerHandler().getRankManager().getRanks().entrySet().stream()
                        .sorted((a, b) -> b.getValue().getPower() - a.getValue().getPower())
                        .map(ListCommand::formatRank)
                        .collect(Collectors.joining(ChatColor.GRAY + ", "))
        ).append(ChatColor.GRAY).append(".\n");

        String players = Core.API.getServer().getOnlinePlayers().stream()
                .sorted(Comparator.comparingInt(a -> {
                    RankEntry rank = Core.API.getManagerHandler().getProfileManager().getRank(((Player) a).getUniqueId());
                    return rank == null ? 0 : rank.getPower();
                }).reversed())
                .map(player -> {
                    String rankColor = Core.API.getManagerHandler().getProfileManager().getRank(player.getUniqueId()).getColor();
                    if (Core.API.getManagerHandler().getProfileManager().getDisguised().containsKey(player.getUniqueId())) {
                        rankColor = ChatColor.GREEN.toString();
                    }
                    return CoreUtils.translate(rankColor) + player.getDisplayName();
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));

        builder.append(ChatColor.GRAY).append("(").
                append(Core.API.getServer().getOnlinePlayers().size()).append("/")
                .append(Core.API.getServer().getMaxPlayers()).append("): ")
                .append(players);

        sender.sendMessage(builder.toString());
    }
}

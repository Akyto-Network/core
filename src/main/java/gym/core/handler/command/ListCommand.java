package gym.core.handler.command;

import gym.core.Core;
import gym.core.utils.Utils;
import gym.core.utils.command.Command;
import gym.core.utils.command.CommandArgs;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class ListCommand {

	@Command(name = "list")
    public void listCommand(final CommandArgs arg) {
    	final CommandSender sender = arg.getSender();
        StringBuilder builder = new StringBuilder();

        builder.append(
                Core.API.getManagerHandler().getRankManager().getRanks().entrySet().stream()
                        .map(rank -> Utils.translate(rank.getValue().getColor()) + StringUtils.capitalize(rank.getKey()))
                        .collect(Collectors.joining(ChatColor.GRAY + ", "))
        ).append(ChatColor.GRAY).append(".\n");

        String players = Core.API.getServer().getOnlinePlayers().stream()
                .map(player -> {
                    String rankColor = Core.API.getManagerHandler().getProfileManager().getRank(player.getUniqueId()).getColor();
                    return Utils.translate(rankColor) + player.getDisplayName();
                })
                .collect(Collectors.joining(ChatColor.GRAY + ", "));

        builder.append(ChatColor.GRAY).append("(").
                append(Core.API.getServer().getOnlinePlayers().size()).append("/")
                .append(Core.API.getServer().getMaxPlayers()).append("): ")
                .append(players);

        sender.sendMessage(builder.toString());
    }
}

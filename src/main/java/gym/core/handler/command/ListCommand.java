package gym.core.handler.command;

import gym.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder builder = new StringBuilder();

        builder.append(
                Core.API.getManagerHandler().getRankManager().getRanks().values().stream()
                        .map(rankEntry -> rankEntry.getColor() + rankEntry.getPrefix())
                        .collect(Collectors.joining(ChatColor.GRAY + ", "))
        ).append(ChatColor.GRAY).append(".\n");

        String players = Core.API.getServer().getOnlinePlayers().stream()
                .map(Player::getDisplayName)
                .collect(Collectors.joining(ChatColor.GRAY + ", "));

        builder.append(ChatColor.GRAY).append("(").
                append(Core.API.getServer().getOnlinePlayers().size()).append("/")
                .append(Core.API.getServer().getMaxPlayers()).append("): ")
                .append(players);

        sender.sendMessage(builder.toString());

        return true;
    }
}

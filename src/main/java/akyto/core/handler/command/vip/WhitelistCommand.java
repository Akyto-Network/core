package akyto.core.handler.command.vip;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.whitelist.WhitelistState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class WhitelistCommand {

    @Command(name = "whitelist", aliases= {"wl"}, inGameOnly = false)
    public void whitelist(final CommandArgs arg) {
        final String helpClassic = ChatColor.RED + "/whitelist add <player>";
        final String[] helpAdmin = new String[]{
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------",
                ChatColor.RED + "/whitelist on/enable <on_list/have_rank>",
                ChatColor.RED + "/whitelist off",
                ChatColor.RED + "/whitelist list",
                " ",
                ChatColor.RED + "/whitelist add <player>",
                ChatColor.RED + "/whitelist remove <player>",
                ChatColor.RED + "/whitelist blacklist <player>",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------"
        };
        final String[] args = arg.getArgs();
        if (args.length == 0 || args.length > 2) {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(helpClassic);
                return;
            }
            else {
                arg.getSender().sendMessage(helpAdmin);
                return;
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable")) {
                if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNoPermission());
                    return;
                }
                Core.API.getManagerHandler().getServerManager().setWhitelistState(WhitelistState.OFF);
                Bukkit.broadcastMessage(Core.API.getLoaderHandler().getMessage().getWhitelistDisabled());
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                String players = Core.API.getWhitelisted().stream()
                        .map(player -> {
                            return Bukkit.getPlayer(player) != null ? ChatColor.GREEN + Bukkit.getPlayer(player).getName() : ChatColor.RED + Bukkit.getOfflinePlayer(player).getName();
                        })
                        .collect(Collectors.joining(ChatColor.GRAY + ", "));
                arg.getSender().sendMessage(new String[]{
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------",
                        ChatColor.DARK_GRAY + "Whitelisted" + ChatColor.GRAY + " (" + ChatColor.RED + Core.API.getWhitelisted().size() + ChatColor.GRAY + "):",
                        players,
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------"
                });
                return;
            }
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(helpClassic);
                return;
            }
            else {
                arg.getSender().sendMessage(helpAdmin);
                return;
            }
        }
        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable")) {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNoPermission());
                return;
            }
            if (WhitelistState.valueOf(args[1].toUpperCase()) != null) {
                Core.API.getManagerHandler().getServerManager().setWhitelistState(WhitelistState.valueOf(args[1].toUpperCase()));
                Bukkit.broadcastMessage(Core.API.getLoaderHandler().getMessage().getWhitelistEnabled()
                        .replace("%type%",
                                WhitelistState.valueOf(args[1].toUpperCase()).equals(WhitelistState.ON_LIST) ? "Global" : " must be have a rank")
                );
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(arg.getPlayer().getUniqueId());
            if (arg.getSender() instanceof Player) {
                final int tokenPrice = Core.API.getLoaderHandler().getSettings().getTokensPriceWhitelist();
                if (profile.getTokens() < tokenPrice && !arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                    final int tokensMissed = Core.API.getLoaderHandler().getSettings().getTokensPriceWhitelist() - profile.getTokens();
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokensMissing()
                            .replace("%tokens%", String.valueOf(tokensMissed))
                    );
                    return;
                }
                if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                    profile.setTokens(profile.getTokens() - tokenPrice);
                }
            }
            Core.API.getWhitelisted().add(args[1].toLowerCase());
            arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistAdd().replace("%player%", args[1]));
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNoPermission());
                return;
            }
            Core.API.getWhitelisted().remove(args[1].toLowerCase());
            arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistRemove().replace("%player%", args[1]));
        } else if (args[0].equalsIgnoreCase("blacklist")) {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNoPermission());
                return;
            }
            Core.API.getWhitelisted().remove(args[1].toLowerCase());
            Core.API.getBlacklistWhitelist().add(args[1].toLowerCase());
            arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistBlacklist().replace("%player%", args[1]));
        } else {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
                arg.getSender().sendMessage(helpClassic);
            }
            else {
                arg.getSender().sendMessage(helpAdmin);
            }
        }
    }

}

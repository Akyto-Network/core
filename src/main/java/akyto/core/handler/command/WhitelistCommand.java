package akyto.core.handler.command;

import akyto.core.Core;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.whitelist.WhitelistState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WhitelistCommand {

    @Command(name = "whitelist", aliases= {"wl"}, inGameOnly = false)
    public void whitelist(final CommandArgs arg) {
        if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getWhitelist())) {
            arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNoPermission());
            return;
        }
        final String[] args = arg.getArgs();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable")) {
                Core.API.getManagerHandler().getServerManager().setWhitelistState(WhitelistState.OFF);
                Bukkit.broadcastMessage(Core.API.getLoaderHandler().getMessage().getWhitelistDisabled());
                return;
            }
            else {
                arg.getSender().sendMessage(new String[]{
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------",
                        ChatColor.RED + "/whitelist on/enable <on_list/have_rank>",
                        ChatColor.RED + "/whitelist off",
                        " ",
                        ChatColor.RED + "/whitelist add <player>",
                        ChatColor.RED + "/whitelist remove <player>",
                        ChatColor.RED + "/whitelist blacklist <player>",
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------"
                });
            }
            return;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable")) {
                if (WhitelistState.valueOf(args[1].toUpperCase()) != null) {
                    Core.API.getManagerHandler().getServerManager().setWhitelistState(WhitelistState.valueOf(args[1].toUpperCase()));
                    Bukkit.broadcastMessage(Core.API.getLoaderHandler().getMessage().getWhitelistEnabled()
                            .replace("%type%",
                                    WhitelistState.valueOf(args[1].toUpperCase()).equals(WhitelistState.ON_LIST) ? "Global" : " must be have a rank")
                    );
                }
                arg.getSender().sendMessage(ChatColor.RED + "/whitelist enable <on_list/have_rank>");
                return;
            }
            else if (args[0].equalsIgnoreCase("add")) {
                Core.API.getWhitelisted().add(args[1].toLowerCase());
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistAdd().replace("%player%", args[1]));
                return;
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                Core.API.getWhitelisted().remove(args[1].toLowerCase());
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistRemove().replace("%player%", args[1]));
                return;
            }
            else if (args[0].equalsIgnoreCase("blacklist")) {
                Core.API.getWhitelisted().remove(args[1].toLowerCase());
                Core.API.getBlacklistWhitelist().add(args[1].toLowerCase());
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getWhitelistBlacklist().replace("%player%", args[1]));
                return;
            }
            else {
                arg.getSender().sendMessage(new String[]{
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------",
                        ChatColor.RED + "/whitelist on/enable <on_list/have_rank>",
                        ChatColor.RED + "/whitelist off",
                        " ",
                        ChatColor.RED + "/whitelist add <player>",
                        ChatColor.RED + "/whitelist remove <player>",
                        ChatColor.RED + "/whitelist blacklist <player>",
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------"
                });
            }
            return;
        }
        arg.getSender().sendMessage(new String[]{
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------",
                ChatColor.RED + "/whitelist on/enable <on_list/have_rank>",
                ChatColor.RED + "/whitelist off",
                " ",
                ChatColor.RED + "/whitelist add <player>",
                ChatColor.RED + "/whitelist remove <player>",
                ChatColor.RED + "/whitelist blacklist <player>",
                ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "----------------------------"
        });
    }

}

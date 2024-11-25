package akyto.core.handler.command.admin;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import akyto.core.utils.format.FormatUtils;
import co.aikar.idb.DB;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class TokensCommand {

    @Command(name = "tokens", aliases= {"coins"})
    public void tokens(final CommandArgs arg) {
        if (arg.getSender() instanceof Player) {
            if (!arg.getSender().hasPermission(Core.API.getLoaderHandler().getPermission().getToken())) {
                final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(arg.getPlayer().getUniqueId());
                arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokenWallet()
                        .replace("%tokens%", String.valueOf(profile.getTokens()))
                );
                return;
            }
        }
        if (arg.getSender().isOp()) {
            if (arg.getArgs().length == 1) {
                if (arg.getArgs().length < 1) {
                    arg.getSender().sendMessage(ChatColor.RED + "/tokens <player>");
                    return;
                }
                if (Bukkit.getPlayer(arg.getArgs()[0]) == null && Bukkit.getOfflinePlayer(arg.getArgs()[0]) == null) {
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNeverPlayed().replace("%player%", arg.getArgs()[1]));
                    return;
                }
                try {
                    if (Core.API.getMySQL().existPlayerManagerAsync(CoreUtils.getUUID(arg.getArgs()[0])).get() && Bukkit.getPlayer(arg.getArgs()[0]) == null) {
                        int currentWalletDB = DB.getFirstRow("SELECT tokens FROM playersdata WHERE name=?", arg.getArgs()[1]).getInt("tokens");
                        arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokenAnotherWallet()
                                .replace("%player%", arg.getArgs()[1])
                                .replace("%tokens%", String.valueOf(currentWalletDB))
                        );
                        return;
                    }
                } catch (InterruptedException | ExecutionException | SQLException e) { throw new RuntimeException(e); }
                if (Bukkit.getPlayer(arg.getArgs()[0]) != null) {
                    final Player target = Bukkit.getPlayer(arg.getArgs()[0]);
                    final Profile targetProfile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId());
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokenAnotherWallet()
                            .replace("%player%", arg.getArgs()[0])
                            .replace("%tokens%", String.valueOf(targetProfile.getTokens()))
                    );
                }
                return;
            }
            if (arg.getArgs().length == 3) {
                if (arg.getArgs()[0].equalsIgnoreCase("add")) {
                    if (arg.getArgs().length < 3) {
                        arg.getSender().sendMessage(ChatColor.RED + "/tokens add <player> <tokens>");
                        return;
                    }
                    if (Bukkit.getPlayer(arg.getArgs()[1]) == null && Bukkit.getOfflinePlayer(arg.getArgs()[1]) == null) {
                        arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNeverPlayed().replace("%player%", arg.getArgs()[1]));
                        return;
                    }
                    if (!FormatUtils.isConvertibleToInt(arg.getArgs()[2])) {
                        arg.getSender().sendMessage(ChatColor.RED + "Please provide a number");
                        return;
                    }
                    final int tokens = Integer.parseInt(arg.getArgs()[2]);
                    try {
                        if (Core.API.getMySQL().existPlayerManagerAsync(CoreUtils.getUUID(arg.getArgs()[1])).get() && Bukkit.getPlayer(arg.getArgs()[1]) == null) {
                            int currentWalletDB = DB.getFirstRow("SELECT tokens FROM playersdata WHERE name=?", arg.getArgs()[1]).getInt("tokens");
                            final int newTokens = currentWalletDB + tokens;
                            DB.executeUpdateAsync("UPDATE playersdata SET tokens=? WHERE name=?", newTokens , arg.getArgs()[1]);
                        }
                    } catch (InterruptedException | ExecutionException | SQLException e) { throw new RuntimeException(e); }
                    if (Bukkit.getPlayer(arg.getArgs()[1]) != null) {
                        final Player target = Bukkit.getPlayer(arg.getArgs()[1]);
                        final Profile targetProfile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId());
                        targetProfile.setTokens(targetProfile.getTokens() + tokens);
                        target.sendMessage(Core.API.getLoaderHandler().getMessage().getTokenReceived().replace("%tokens%", arg.getArgs()[2]));
                    }
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokenGived().replace("%player%", arg.getArgs()[1]).replace("%tokens%", arg.getArgs()[2]));
                    return;
                }
                if (arg.getArgs()[0].equalsIgnoreCase("remove")) {
                    if (arg.getArgs().length < 3) {
                        arg.getSender().sendMessage(ChatColor.RED + "/tokens remove <player> <tokens>");
                        return;
                    }
                    if (Bukkit.getPlayer(arg.getArgs()[1]) == null && Bukkit.getOfflinePlayer(arg.getArgs()[1]) == null) {
                        arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getNeverPlayed().replace("%player%", arg.getArgs()[1]));
                        return;
                    }
                    if (!FormatUtils.isConvertibleToInt(arg.getArgs()[2])) {
                        arg.getSender().sendMessage(ChatColor.RED + "Please provide a number");
                        return;
                    }
                    int tokens = Integer.parseInt(arg.getArgs()[2]);
                    try {
                        if (Core.API.getMySQL().existPlayerManagerAsync(CoreUtils.getUUID(arg.getArgs()[1])).get() && Bukkit.getPlayer(arg.getArgs()[1]) == null) {
                            int currentWalletDB = DB.getFirstRow("SELECT tokens FROM playersdata WHERE name=?", arg.getArgs()[1]).getInt("tokens");
                            final int newTokens = currentWalletDB - tokens;
                            DB.executeUpdateAsync("UPDATE playersdata SET tokens=? WHERE name=?", newTokens , arg.getArgs()[1]);
                        }
                    } catch (InterruptedException | ExecutionException | SQLException e) { throw new RuntimeException(e); }
                    if (Bukkit.getPlayer(arg.getArgs()[1]) != null) {
                        final Player target = Bukkit.getPlayer(arg.getArgs()[1]);
                        final Profile targetProfile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(target.getUniqueId());
                        if (targetProfile.getTokens() < tokens) tokens = targetProfile.getTokens();
                        targetProfile.setTokens(targetProfile.getTokens() - tokens);
                    }
                    arg.getSender().sendMessage(Core.API.getLoaderHandler().getMessage().getTokenRemoved().replace("%player%", arg.getArgs()[1]).replace("%tokens%", arg.getArgs()[2]));
                }
            }
            else {
                arg.getSender().sendMessage(new String[] {
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------------------",
                        ChatColor.RED + "/tokens add <player> <amount>",
                        ChatColor.RED + "/tokens remove <player> <amount>",
                        " ",
                        ChatColor.RED + "/tokens <player>",
                        ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------------------"
                });
            }
        }
    }

}

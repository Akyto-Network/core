package akyto.core.handler.command.commons;

import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.team.TeamEntry;
import akyto.core.utils.CoreUtils;
import akyto.core.utils.command.Command;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(name = "team", aliases = {"teams", "club", "crew", "clan", "clans"})
    public void teamCommand(final CommandArgs arg) {
        final int args = arg.getArgs().length;
        final Player player = arg.getPlayer();
        if (args == 0) {
            player.sendMessage(new String[] {
                    ChatColor.RED + "/team create <name>",
                    ChatColor.RED + "/team leave/disband",
                    ChatColor.RED + "/team promote <player>",
                    ChatColor.RED + "/team invite <player>",
                    ChatColor.RED + "/team kick <player>"
            });
            return;
        }
        final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfile(player.getUniqueId());
        if (arg.getArgs()[0].equalsIgnoreCase("create")) {
            if (args != 1) {
                player.sendMessage(ChatColor.RED + "/team create <name>");
                return;
            }
            if (profile.getTeam().equalsIgnoreCase("none")) {
                player.sendMessage(ChatColor.RED + "You cannot do this because you are already in a team, leave or disband it to create another one.");
                return;
            }
            final String teamName = arg.getArgs(1);
            if (Core.API.getTeams().get(teamName) != null) {
                player.sendMessage(ChatColor.RED + "Another team with this name already exist please chose another name.");
                return;
            }
            final TeamEntry teamEntry = new TeamEntry(teamName, player.getUniqueId());
            Core.API.getTeams().putIfAbsent(teamName, teamEntry);
            profile.setTeam(teamName);
            player.sendMessage(ChatColor.GREEN + "You've successfully created the team " + ChatColor.WHITE + teamName + ChatColor.GREEN + " !");
        }
        else if (arg.getArgs()[0].equalsIgnoreCase("leave")) {
            if (profile.getTeam().equalsIgnoreCase("none")) {
                player.sendMessage(ChatColor.RED + "You cannot do this because you are not into any team!");
                return;
            }
            final String teamName = profile.getTeam();
            if (Core.API.getTeams().get(teamName).getLeader().equals(player.getUniqueId())) {
                if (!Core.API.getTeams().get(teamName).getMembers().isEmpty()) {
                    Core.API.getTeams().get(teamName).getMembers().forEach(members -> {
                        Core.API.getManagerHandler().getProfileManager().getProfile(members).setTeam("none");
                        if (Bukkit.getPlayer(members) != null) {
                            Bukkit.getPlayer(members).sendMessage(ChatColor.RED + player.getName() + " disband the team!");
                        }
                    });
                }
                Core.API.getTeams().remove(teamName);
                return;
            }
            Core.API.getTeams().get(teamName).getMembers().forEach(members -> {
                if (Bukkit.getPlayer(members) != null) {
                    Bukkit.getPlayer(members).sendMessage(ChatColor.RED + player.getName() + " leave the " + teamName + " team!");
                }
            });
            Core.API.getTeams().get(teamName).getMembers().remove(player.getUniqueId());
        }
    }
}

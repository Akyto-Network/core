package akyto.bot.listener;

import akyto.bot.command.LinkCommand;
import akyto.core.Core;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscordListener extends ListenerAdapter {

    private Core main;
    private final LinkCommand linkCommand;

    public DiscordListener(final Core main) {
        this.main = main;
        this.linkCommand = main.linkCommand;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("link")) {
            if (!event.getChannel().getId().equals("1368350429947236415")) {
                event.reply("> You can only use the /link command in the " + Core.API.getJda().getTextChannelById("1368350429947236415").getAsMention() + " channel").setEphemeral(true).queue();
                return;

            } else {
                String code = Objects.requireNonNull(event.getOption("code")).getAsString();
                UUID uuid = getUUIDFromCode(code);
                if (uuid != null) {
                    if (Core.API.getLinkedId().contains(event.getUser().getId())) {
                        event.reply("Your account was already linked!").setEphemeral(true).queue();
                        return;
                    }
                    if (Bukkit.getPlayer(uuid) == null) {
                        event.reply("You need to be connected to execute this command, connect on Akyto.net and retry!").setEphemeral(true).queue();
                        return;
                    }
                    Member member = event.getMember();
                    Guild guild = event.getGuild();
                    Role defaultRole = guild.getRoleById("1369867811702177873");
                    if (defaultRole == null) {
                        event.reply("> Error 404. Please try later.").setEphemeral(true).queue();
                        return;
                    }
                    guild.addRoleToMember(member, defaultRole).queue();
                    Core.API.getManagerHandler().getProfileManager().getProfile(uuid).setLink(1);
                    Core.API.getManagerHandler().getProfileManager().getProfile(uuid).setLinked();
                    event.reply("> Your Discord account has been linked to your Minecraft account!").setEphemeral(true).queue();
                    Core.API.getLinkedId().add(event.getUser().getId());
                    final Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");
                    player.sendMessage(ChatColor.GREEN + "Your Discord account has been successfully linked!");
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You can join ranked queue now.");
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");
                    linkCommand.removeCode(code);
                } else {
                    event.reply("Invalid verification code.").setEphemeral(true).queue();
                }
            }
        } if (event.getName().equals("whitelist")) {
            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE)) {
                String username = Objects.requireNonNull(event.getOption("username")).getAsString();
                if (Core.API.getWhitelisted().contains(username)) {
                    Core.API.getWhitelisted().remove(username);
                    System.out.println(Core.API.getWhitelisted());
                    event.reply(username + " as removed from the whitelist").setEphemeral(true).queue();
                    return;
                }
                Core.API.getWhitelisted().add(username);
                event.reply(username + " added on the whitelist").setEphemeral(true).queue();
            } else {
                event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            }
        }
    }

    public UUID getUUIDFromCode(String code) {
        return LinkCommand.verificationCodes.get(code);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("1368350429947236415")) {
            Message message = event.getMessage();
            String content = message.getContentRaw();

            if (!content.equals("/link")) {
                message.delete().queue();
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("link", "link your account discord to minecraft").addOption(OptionType.STRING, "code", "Verification Code for link discord to mc acc", true));
        commandData.add(Commands.slash("whitelist", "Add or remove someone to whitelist").addOption(OptionType.STRING, "username", "Username to add or remove from whitelist", true));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}

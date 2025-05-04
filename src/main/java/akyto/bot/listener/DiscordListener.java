package akyto.bot.listener;

import akyto.bot.command.LinkCommand;
import akyto.core.Core;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {

    private Core main;
    private final LinkCommand linkCommand;

    public DiscordListener(final Core main) {
        this.main = main;
        this.linkCommand = main.linkCommand;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
            Core.API.getManagerHandler().getProfileManager().getProfile(uuid).setLink(1);
            Core.API.getManagerHandler().getProfileManager().getProfile(uuid).setLinked();
            event.reply("Your Discord account has been linked to your Minecraft account!").setEphemeral(true).queue();
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

    public UUID getUUIDFromCode(String code) {
        return LinkCommand.verificationCodes.get(code);
    }

    @Override
    public void onReady(ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("link", "link your account discord to minecraft").addOption(OptionType.STRING, "code", "Verification Code for link discord to mc acc", true));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }
}

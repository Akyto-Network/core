package akyto.bot.command;

import akyto.bot.generator.VerificationCodeGenerator;
import akyto.core.Core;
import akyto.core.profile.Profile;
import akyto.core.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkCommand {

    public static final Map<String, UUID> verificationCodes = new HashMap<>();

    @akyto.core.utils.command.Command(name = "link", aliases= {"verify"}, inGameOnly = true)
    public void linkCommand(final CommandArgs arg){
        if (!(arg.getSender() instanceof Player)) {
            arg.getSender().sendMessage("Only players can use this command.");
            return;
        }

        Player player = arg.getPlayer();
        final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfile(player.getUniqueId());
        if (profile.isLinked()) {
            player.sendMessage(ChatColor.RED + "Your account was already linked!");
            return;
        }
        if (verificationCodes.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Use your previous code after make a new one.");
            return;
        }
        String code = VerificationCodeGenerator.generateCode();
        verificationCodes.put(code, player.getUniqueId());

        player.sendMessage(" ");
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");
        player.sendMessage(ChatColor.GRAY + " » " + ChatColor.YELLOW + "Your link code is" + ChatColor.GRAY + ": ");
        player.sendMessage(" ");
        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "        " + code);
        player.sendMessage(" ");
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Please send this code in the '#┃ link' channel on Discord.");
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Do not share this unique code to anyone");
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");
        player.sendMessage(" ");
    }



    public void removeCode(String code) {
        verificationCodes.remove(code);
    }
}


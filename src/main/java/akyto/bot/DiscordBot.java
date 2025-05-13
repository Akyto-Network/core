package akyto.bot;

import akyto.bot.listener.DiscordListener;
import akyto.core.Core;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class DiscordBot {

    public DiscordBot(final String botToken, final Core main) {
        main.jda = JDABuilder.createLight(botToken, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.customStatus("/link | akyto.net"))
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MODERATION)
                .build();
        main.jda.addEventListener(new DiscordListener(main));
    }
}

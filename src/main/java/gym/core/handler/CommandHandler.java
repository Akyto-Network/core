package gym.core.handler;

import java.util.Arrays;

import gym.core.Core;
import gym.core.handler.command.*;
import gym.core.utils.command.CommandFramework;
import org.bukkit.plugin.Plugin;

public class CommandHandler {

	public CommandHandler(final Core main) {
		this.setupFramework(main,
				new AnnounceCommand(),
				new PunishmentsCommand(),
				new ChatCommand(),
				new FreezeCommand(),
				new HubCommand(),
				new ListCommand(),
				new MessageCommand(),
				new ModCommand(),
				new RankCommand(),
				new ReportCommand(),
				new SocialsCommand(),
				new TimeCommand(),
				new MoreCommand(),
				new HeadCommand(),
				new TestCommand()
		);
	}

	private void setupFramework(final Plugin plugin, final Object... objects) {
        CommandFramework commandFramework = new CommandFramework(plugin);
        Arrays.stream(objects).forEach(commandFramework::registerCommands);
	}

}

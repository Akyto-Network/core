package akyto.core.handler;

import java.util.Arrays;

import akyto.core.Core;
import akyto.core.handler.command.*;
import akyto.core.utils.command.CommandFramework;
import org.bukkit.plugin.Plugin;

public class CommandHandler {

	public CommandHandler(final Core main) {
		this.setupFramework(main,
				new AnnounceCommand(),
				new ParticleCommand(),
				new DisguiseCommand(),
				new PunishmentsCommand(),
				new ChatCommand(),
				new FreezeCommand(),
				new BreedCommand(),
				new WhitelistCommand(),
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

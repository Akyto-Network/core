package gym.core.handler;

import java.util.Arrays;

import gym.core.Core;
import gym.core.handler.command.*;
import gym.core.utils.command.CommandFramework;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;

public class CommandHandler {
	
	public CommandHandler(final Core main) {
		this.setupFramework(
				new AnnounceCommand(),
				new BanCommand(),
				new ChatCommand(),
				new FreezeCommand(),
				new HubCommand(),
				new ListCommand(),
				new MessageCommand(),
				new ModCommand(),
				new MuteCommand(),
				new RankCommand(),
				new ReportCommand(),
				new SocialsCommand(),
				new TimeCommand(),
				new MoreCommand(),
				new HeadCommand(),
				new ViewCpsCommand());
	}

	private void setupFramework(final Object... objects) {
        CommandFramework commandFramework = new CommandFramework(Core.API);
        Arrays.stream(objects).forEach(commandFramework::registerCommands);
	}

}

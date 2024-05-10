package gym.core.handler;

import java.util.Arrays;

import gym.core.Core;
import gym.core.handler.command.AnnounceCommand;
import gym.core.handler.command.BanCommand;
import gym.core.handler.command.ChatCommand;
import gym.core.handler.command.FreezeCommand;
import gym.core.handler.command.HubCommand;
import gym.core.handler.command.ListCommand;
import gym.core.handler.command.MessageCommand;
import gym.core.handler.command.ModCommand;
import gym.core.handler.command.MuteCommand;
import gym.core.handler.command.RankCommand;
import gym.core.handler.command.ReportCommand;
import gym.core.handler.command.SocialsCommand;
import gym.core.handler.command.TimeCommand;
import gym.core.handler.command.ViewCpsCommand;
import gym.core.utils.command.CommandFramework;

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
				new ViewCpsCommand());
	}

	private void setupFramework(final Object... objects) {
        CommandFramework commandFramework = new CommandFramework(Core.API);
        Arrays.stream(objects).forEach(commandFramework::registerCommands);
	}

}

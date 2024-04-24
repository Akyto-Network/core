package gym.core.handler;

import gym.core.Core;
import gym.core.handler.command.BanCommand;
import gym.core.handler.command.ChatCommand;
import gym.core.handler.command.FreezeCommand;
import gym.core.handler.command.MuteCommand;
import gym.core.handler.command.RankCommand;
import gym.core.handler.command.ViewCpsCommand;

public class CommandHandler {
	
	public CommandHandler(final Core main) {
		main.getCommand("rank").setExecutor(new RankCommand(main));
		main.getCommand("ban").setExecutor(new BanCommand(main));
		main.getCommand("unban").setExecutor(new BanCommand(main));
		main.getCommand("mute").setExecutor(new MuteCommand(main));
		main.getCommand("unmute").setExecutor(new MuteCommand(main));
		main.getCommand("viewcps").setExecutor(new ViewCpsCommand(main));
		main.getCommand("chat").setExecutor(new ChatCommand(main));
		main.getCommand("freeze").setExecutor(new FreezeCommand(main));
	}

}

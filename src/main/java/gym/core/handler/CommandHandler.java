package gym.core.handler;

import gym.core.Core;
import gym.core.handler.command.AnnounceCommand;
import gym.core.handler.command.BanCommand;
import gym.core.handler.command.BulldogCommand;
import gym.core.handler.command.ChatCommand;
import gym.core.handler.command.FreezeCommand;
import gym.core.handler.command.MessageCommand;
import gym.core.handler.command.ModCommand;
import gym.core.handler.command.MuteCommand;
import gym.core.handler.command.RankCommand;
import gym.core.handler.command.ReportCommand;
import gym.core.handler.command.TimeCommand;
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
		main.getCommand("mod").setExecutor(new ModCommand(main));
		main.getCommand("night").setExecutor(new TimeCommand(main));
		main.getCommand("day").setExecutor(new TimeCommand(main));
		main.getCommand("sunset").setExecutor(new TimeCommand(main));
		main.getCommand("report").setExecutor(new ReportCommand(main));
		main.getCommand("msg").setExecutor(new MessageCommand(main));
		main.getCommand("reply").setExecutor(new MessageCommand(main));
		main.getCommand("announce").setExecutor(new AnnounceCommand());
		main.getCommand("bulldog").setExecutor(new BulldogCommand());
	}

}

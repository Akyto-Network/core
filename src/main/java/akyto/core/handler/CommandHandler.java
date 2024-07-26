package akyto.core.handler;

import java.util.Arrays;

import akyto.core.Core;
import akyto.core.handler.command.*;
import akyto.core.handler.command.admin.*;
import akyto.core.handler.command.admin.rank.DemoteCommand;
import akyto.core.handler.command.admin.rank.PromoteCommand;
import akyto.core.handler.command.admin.rank.RankCommand;
import akyto.core.handler.command.commons.*;
import akyto.core.handler.command.moderation.FreezeCommand;
import akyto.core.handler.command.moderation.ModCommand;
import akyto.core.handler.command.moderation.punishment.ban.BanCommand;
import akyto.core.handler.command.moderation.punishment.ban.UnbanCommand;
import akyto.core.handler.command.moderation.punishment.blacklist.BlacklistCommand;
import akyto.core.handler.command.moderation.punishment.blacklist.UnblacklistCommand;
import akyto.core.handler.command.moderation.punishment.mute.MuteCommand;
import akyto.core.handler.command.moderation.punishment.mute.UnmuteCommand;
import akyto.core.handler.command.vip.DisguiseCommand;
import akyto.core.handler.command.vip.GiveawayCommand;
import akyto.core.handler.command.vip.WhitelistCommand;
import akyto.core.utils.command.CommandFramework;
import org.bukkit.plugin.Plugin;

public class CommandHandler {

	CommandFramework commandFramework = new CommandFramework(Core.API);

	public CommandHandler(final Core main) {
		this.setupFramework(main,

				// ADMIN
				new AnnounceCommand(),
				new TokensCommand(),
				new ChatCommand(),
				new MoreCommand(),
				new HeadCommand(),
				new BreedCommand(),
				// RANK
				new RankCommand(),
				new PromoteCommand(),
				new DemoteCommand(),

				// MODERATION
				new BanCommand(),
				new UnbanCommand(),
				new MuteCommand(),
				new UnmuteCommand(),
				new BlacklistCommand(),
				new UnblacklistCommand(),
				new FreezeCommand(),
				new ModCommand(),

				// VIP
				new DisguiseCommand(),
				new WhitelistCommand(),
				new GiveawayCommand(),

				// COMMONS
				new ListCommand(),
				new MessageCommand(),
				new ReportCommand(),
				new SocialsCommand(),
				new TimeCommand(),
				new FriendCommand(),

				new HubCommand()
		);
	}

	private void setupFramework(final Object... objects) {
        Arrays.stream(objects).forEach(commandFramework::registerCommands);
	}

}

package akyto.core.handler.manager;

import akyto.core.Core;
import akyto.core.chat.ChatPriority;
import akyto.core.chat.ChatState;
import akyto.core.disguise.DisguiseEntry;
import akyto.core.particle.ParticleEntry;
import akyto.core.whitelist.WhitelistState;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class ServerManager {
	
	private ChatState chatState;
	private ChatPriority chatPriority;
	private WhitelistState whitelistState;

	private HashMap<String, DisguiseEntry> disguise = new HashMap<>();
	
	public ServerManager(final String chatState, final String chatPriority) {
		this.chatState = ChatState.valueOf(chatState.toUpperCase());
		this.chatPriority = ChatPriority.valueOf(chatPriority.toUpperCase());
		this.whitelistState = WhitelistState.valueOf(Core.API.getConfig().getString("whitelist.state"));
	}
	
	public boolean chatIsClosed() {
		return this.chatState.equals(ChatState.CLOSED);
	}

}

package akyto.core.handler.manager;

import akyto.core.chat.ChatPriority;
import akyto.core.chat.ChatState;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerManager {
	
	private ChatState chatState;
	private ChatPriority chatPriority;
	
	public ServerManager(final String chatState, final String chatPriority) {
		this.chatState = ChatState.valueOf(chatState.toUpperCase());
		this.chatPriority = ChatPriority.valueOf(chatPriority.toUpperCase());
	}
	
	public boolean chatIsClosed() {
		return this.chatState.equals(ChatState.CLOSED);
	}

}

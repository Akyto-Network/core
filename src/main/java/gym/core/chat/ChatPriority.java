package gym.core.chat;

import lombok.Getter;

@Getter
public enum ChatPriority {
	
	HIGH(15),
	MEDIUM(10),
	LOWER(5),
	NORMAL(3),
	SPAM(0);
	
	final int time;
	
	ChatPriority(final int time) {
		this.time = time;
	}
}

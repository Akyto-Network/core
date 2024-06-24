package gym.core.punishment.cache;

import lombok.Getter;

@Getter
public class BanEntry {
	
	private final String expiresOn;
	private final String reason;
	private final String judge;
	
	public BanEntry(final String expiresOn, final String reason, String judge) {
		this.expiresOn = expiresOn;	
		this.reason = reason;
		this.judge = judge;
	}

}

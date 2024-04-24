package gym.core.punishment;

import lombok.Getter;

@Getter
public class BanEntry {
	
	private String expiresOn;
	private String reason;
	private String judge;
	
	public BanEntry(final String expiresOn, final String reason, String judge) {
		this.expiresOn = expiresOn;	
		this.reason = reason;
		this.judge = judge;
	}

}

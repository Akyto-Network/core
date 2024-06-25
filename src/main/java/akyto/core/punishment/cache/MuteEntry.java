package akyto.core.punishment.cache;

import lombok.Getter;

@Getter
public class MuteEntry {
	
	private final String expiresOn;
	private final String reason;
	private final String judge;
	
	public MuteEntry(final String expiresOn, final String reason, String judge) {
		this.expiresOn = expiresOn;	
		this.reason = reason;
		this.judge = judge;
	}

}

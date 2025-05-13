package akyto.core.punishment.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class WarnEntry {
	
	private Integer counter;
	private List<String> warner;
	
	public WarnEntry(final int counter, final List<String> warner) {
		this.counter = counter;
		this.warner = warner;
	}
}

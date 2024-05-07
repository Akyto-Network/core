package gym.core.punishment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WarnEntry {
	
	private Integer counter;
	private List<String> warner;
	
	public WarnEntry(final int counter, final List<String> warner) {
		this.counter = counter;
		this.warner = warner;
	}
}

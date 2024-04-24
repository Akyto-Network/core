package gym.core.punishment;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WarnEntry {
	
	private Integer counter;
	private List<String> warner = Lists.newArrayList();
	
	public WarnEntry(final int counter, final List<String> warner) {
		this.counter = counter;
		this.warner = warner;
	}

}

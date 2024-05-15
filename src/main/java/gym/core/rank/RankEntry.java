package gym.core.rank;

import java.util.List;

import gym.core.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RankEntry {
	
	private String prefix;
	public String getPrefix() { return Utils.translate(this.prefix); }
	private String color;
	private Boolean spaceBetweenColor;
	private int power;
	private List<String> permissions;
	
	public RankEntry(final String prefix, final String color, final Boolean spacer, final List<String> permissions, final int power) {
		this.prefix = prefix;
		this.color = color;
		this.spaceBetweenColor = spacer;
		this.power = power;
		this.permissions = permissions;
	}

	public Boolean hasSpaceBetweenColor() {
		return this.spaceBetweenColor;
	}
}
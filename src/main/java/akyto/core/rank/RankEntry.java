package akyto.core.rank;

import java.util.List;

import akyto.core.utils.CoreUtils;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RankEntry {
	
	private String prefix;
	public String getPrefix() { return CoreUtils.translate(this.prefix); }
	private String color;
	private Boolean spaceBetweenColor;
	private int power;
	private Boolean canJoinWhitelist;
	private List<String> permissions;
	
	public RankEntry(final String prefix, final String color, final Boolean spacer, final Boolean canJoinWhitelist, final List<String> permissions, final int power) {
		this.prefix = prefix;
		this.color = color;
		this.spaceBetweenColor = spacer;
		this.canJoinWhitelist = canJoinWhitelist;
		this.power = power;
		this.permissions = permissions;
	}

	public Boolean hasRankWhitelist() {
		return this.canJoinWhitelist;
	}

	public Boolean hasSpaceBetweenColor() {
		return this.spaceBetweenColor;
	}
}
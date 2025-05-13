package akyto.core.handler.manager;

import akyto.core.Core;
import akyto.core.rank.RankEntry;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class RankManager {
	
	private final Core main;
	
	private final HashMap<String, RankEntry> ranks;
	private final List<String> deletedRank;
	
	public RankManager(final Core main) {
		this.main = main;
		this.ranks = new HashMap<>();
		this.deletedRank = new ArrayList<>();
	}
	
	public void createRank(final String name) {
		this.ranks.put(name, new RankEntry(ChatColor.GREEN.toString(), ChatColor.GREEN.toString() ,false, false, Lists.newArrayList(), 0));
	}

}

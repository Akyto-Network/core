package akyto.core.handler.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

import com.google.common.collect.Lists;

import akyto.core.Core;
import akyto.core.rank.RankEntry;
import lombok.Getter;

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
		this.ranks.put(name, new RankEntry(ChatColor.GREEN.toString(), ChatColor.GREEN.toString(), false, Lists.newArrayList(), 0));
	}

}

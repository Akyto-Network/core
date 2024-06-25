package akyto.core.handler.manager;

import akyto.core.Core;
import akyto.core.rank.RankEntry;
import akyto.core.tag.TagEntry;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagManager {

    private final Core main;

    @Getter
    private final HashMap<String, TagEntry> tags;
    private final List<String> deletedTag;

    public TagManager(final Core main) {
        this.main = main;
        this.tags = new HashMap<>();
        this.deletedTag = new ArrayList<>();
    }

    public void createTag(final String name) {
        this.tags.put(name, new TagEntry(ChatColor.GREEN.toString()));
    }

}

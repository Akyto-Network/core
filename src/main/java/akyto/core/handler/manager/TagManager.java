package akyto.core.handler.manager;

import akyto.core.Core;
import akyto.core.rank.RankEntry;
import akyto.core.tag.TagEntry;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagManager {

    @Getter
    private final HashMap<String, TagEntry> tags;

    public TagManager() {
        this.tags = new HashMap<>();
    }

}

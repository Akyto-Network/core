package akyto.core.handler.manager;

import akyto.core.tag.TagEntry;
import lombok.Getter;

import java.util.HashMap;

public class TagManager {

    @Getter
    private final HashMap<String, TagEntry> tags;

    public TagManager() {
        this.tags = new HashMap<>();
    }

}

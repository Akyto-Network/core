package akyto.core.tag;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class TagEntry {

    private final String prefix;
    private final String category;
    private final Material icon;

    public TagEntry(final String prefix, final String category, final Material icon) {
        this.prefix = prefix;
        this.category = category;
        this.icon = icon;
    }
}

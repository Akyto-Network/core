package akyto.core.tag;

import akyto.core.utils.CoreUtils;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class TagEntry {

    private final String prefix;
    private final String permissions;
    private final int price;
    private final Material icon;

    public TagEntry(final String prefix, final String permissions, final int price, final Material icon) {
        this.prefix = prefix;
        this.permissions = permissions;
        this.price = price;
        this.icon = icon;
    }

    public String getPrefix() {
        return CoreUtils.translate(this.prefix);
    }
}

package akyto.core.handler.loader;

import akyto.core.Core;
import akyto.core.utils.CoreUtils;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Inventories {

	private final String frozeName;
	private final String displayFrozen;
	private final Material frozenMaterial;
	private final List<String> loreFrozen = new ArrayList<>();
	
	public Inventories(final Core main) {
		this.frozeName = CoreUtils.translate(main.getConfig().getString("freeze.inventory.title"));
		this.displayFrozen = CoreUtils.translate(main.getConfig().getString("freeze.inventory.display"));
		this.frozenMaterial = Material.valueOf(main.getConfig().getString("freeze.inventory.item"));
		main.getConfig().getStringList("freeze.inventory.lore").forEach(str -> this.loreFrozen.add(CoreUtils.translate(str)));
	}
}

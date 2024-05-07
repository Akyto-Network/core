package gym.core.handler.loader;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import gym.core.Core;
import gym.core.utils.Utils;
import lombok.Getter;

@Getter
public class Inventories {

	private final String frozeName;
	private final String displayFrozen;
	private final Material frozenMaterial;
	private final List<String> loreFrozen = new ArrayList<>();
	
	public Inventories(final Core main) {
		this.frozeName = Utils.translate(main.getConfig().getString("freeze.inventory.title"));
		this.displayFrozen = Utils.translate(main.getConfig().getString("freeze.inventory.display"));
		this.frozenMaterial = Material.valueOf(main.getConfig().getString("freeze.inventory.item"));
		main.getConfig().getStringList("freeze.inventory.lore").forEach(str -> this.loreFrozen.add(Utils.translate(str)));
	}
}

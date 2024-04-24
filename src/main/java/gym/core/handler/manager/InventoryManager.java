package gym.core.handler.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gym.core.Core;
import gym.core.handler.loader.Inventories;
import lombok.Getter;

public class InventoryManager {
	
	private Core main;
	private Inventories invConfig;
	@Getter
	private Inventory frozeInventory;
	
	public InventoryManager(final Core main) {
		this.main = main;
		this.invConfig = main.getLoaderHandler().getInventory();
		this.frozeInventory = Bukkit.createInventory(null, InventoryType.DISPENSER, main.getLoaderHandler().getInventory().getFrozeName());
		this.setFrozenInventory();
	}

	private void setFrozenInventory() {
		for (int i = 0; i < 9; i++) {
			this.frozeInventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}
		this.frozeInventory.setItem(3, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
		this.frozeInventory.setItem(4, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
		this.frozeInventory.setItem(5, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
	}
	
	
	private ItemStack createItems(final Material material, final String name, final int amount, final byte id, final List<String> lore) {
		final ItemStack item = new ItemStack(material, amount, id);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}

package akyto.core.handler.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import akyto.core.Core;
import akyto.core.particle.ParticleEntry;
import akyto.core.utils.CoreUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import akyto.core.handler.loader.Inventories;
import akyto.core.profile.Profile;
import akyto.core.utils.item.ItemUtils;
import lombok.Getter;

public class InventoryManager {
	
	private final Inventories invConfig;
	@Getter
	private final Inventory frozeInventory;
    @Getter
    private final Inventory[] tagInventory;
	@Getter
	private final ConcurrentHashMap<UUID, Inventory> profileInventory;
    @Getter
    private final Inventory particlesInventory;
    @Getter
    private final ConcurrentHashMap<UUID, Inventory> commonTags = new ConcurrentHashMap<>();
	
	public InventoryManager() {
		this.invConfig = Core.API.getLoaderHandler().getInventory();
        this.profileInventory = new ConcurrentHashMap<>();
        this.tagInventory = new Inventory[9];
        this.tagInventory[0] = Bukkit.createInventory(null, 9, ChatColor.GRAY + "Tags:");
        this.tagInventory[1] = Bukkit.createInventory(null, 27, ChatColor.GRAY + "Tags Common:");
		this.frozeInventory = Bukkit.createInventory(null, InventoryType.DISPENSER, Core.API.getLoaderHandler().getInventory().getFrozeName());
        this.particlesInventory = Bukkit.createInventory(null, Core.API.getParticlesFile().getConfig().getInt("inventory.size"), CoreUtils.translate(Core.API.getParticlesFile().getConfig().getString("inventory.name")));
		this.setFrozenInventory();
	}

    public void generateCommonTagInventory(final UUID uuid) {
        final Inventory clone = Bukkit.createInventory(null, this.tagInventory[1].getSize(), this.tagInventory[1].getName());
        final Profile profile = Core.API.getManagerHandler().getProfileManager().getProfiles().get(uuid);
        Core.API.getManagerHandler().getTagManager().getTags().forEach((name, tagEntry) -> {
            final List<String> lores = Lists.newArrayList();
            lores.add(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------");
            lores.add(ChatColor.GRAY + "Owned: " + (Bukkit.getPlayer(uuid).hasPermission(tagEntry.getPermissions()) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
            lores.add(ChatColor.GRAY + "Preview: " + tagEntry.getPrefix());
            lores.add(" ");
            if (!Bukkit.getPlayer(uuid).hasPermission(tagEntry.getPermissions())) {
                lores.add(ChatColor.GRAY + "Price: " + ChatColor.RED + tagEntry.getPrice() + "tokens");
                lores.add(ChatColor.GRAY + "Click to buy it!");
            }
            if (profile.getTag().equals(name)) lores.add(ChatColor.GREEN + "Currently set");
            if (!profile.getTag().equals(name) && Bukkit.getPlayer(uuid).hasPermission(tagEntry.getPermissions())) lores.add(ChatColor.GRAY + "Click for set it");
            lores.add(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "-----------------");
            final ItemStack item = ItemUtils.createItems(
                    tagEntry.getIcon(),
                    ChatColor.GRAY + name,
                    lores
            );
            clone.addItem(item);
        });
        this.commonTags.remove(uuid);
        this.commonTags.put(uuid, clone);
    }

    private void setFrozenInventory() {
		for (int i = 0; i < 9; i++) {
			this.frozeInventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}
		this.frozeInventory.setItem(3, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
		this.frozeInventory.setItem(4, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
		this.frozeInventory.setItem(5, this.createItems(this.invConfig.getFrozenMaterial(), this.invConfig.getDisplayFrozen(), 1, (byte) 0, this.invConfig.getLoreFrozen()));
	}
	
	public void generateProfileInventory(final UUID uuid, final int kitSize, final String[] kitNames) {
		final Inventory profile = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.DARK_GRAY + CoreUtils.getName(uuid) + " profile");
		final Profile profiles = Core.API.getManagerHandler().getProfileManager().getProfiles().get(uuid);
        final ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)8);
        for (int i = 0; i < 5; ++i) {
            profile.setItem(i, glass);
        }
        List<String> kitLore = new ArrayList<>();
        kitLore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        for (int i = 0; i < kitSize; i++) {
        	final int lose = profiles.getStats().get(0)[i] - profiles.getStats().get(1)[i];
        	kitLore.add(kitNames[i] + ChatColor.GRAY + " (" + ChatColor.WHITE + profiles.getStats().get(2)[i] + ChatColor.GRAY + ") : " + ChatColor.GREEN + profiles.getStats().get(1)[i] +  ChatColor.GRAY + "/" + ChatColor.YELLOW + profiles.getStats().get(0)[i] + ChatColor.GRAY + "/" + ChatColor.RED + lose);
        }
        kitLore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        List<String> playerLore = new ArrayList<>();
        int totalWins = 0;
        int totalPlayed = 0;
        int totalElos = 0;
        playerLore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        for (int i = 0; i < kitSize; i++) {
            totalWins = totalWins + profiles.getStats().get(1)[i];
            totalPlayed = totalPlayed + profiles.getStats().get(0)[i];
            totalElos = totalElos + profiles.getStats().get(2)[i];
        }
        playerLore.add(ChatColor.DARK_GRAY + "Total Win" + ChatColor.GRAY + ": " + ChatColor.WHITE + totalWins);
        playerLore.add(ChatColor.DARK_GRAY + "Total Played" + ChatColor.GRAY + ": " + ChatColor.WHITE + totalPlayed);
        int loose = totalPlayed - totalWins;
        playerLore.add(ChatColor.DARK_GRAY + "Total Loose" + ChatColor.GRAY + ": " + ChatColor.WHITE + loose);
        playerLore.add(" ");
        int globalElos = totalElos / kitSize;
        playerLore.add(ChatColor.DARK_GRAY + "Global Elo" + ChatColor.GRAY + ": " + ChatColor.WHITE + globalElos);
        playerLore.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        meta.setOwner(CoreUtils.getName(uuid));
        meta.setDisplayName(ChatColor.GRAY + "Player Statistics");
        meta.setLore(playerLore);
        playerHead.setItemMeta(meta);
        profile.setItem(1, ItemUtils.createItems(Material.BREWING_STAND_ITEM, ChatColor.GRAY + "Kit Statistics" + ChatColor.GRAY + ":", kitLore));
        profile.setItem(3, playerHead);
        this.profileInventory.remove(uuid);
		this.profileInventory.put(uuid, profile);
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

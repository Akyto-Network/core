package akyto.core.profile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import akyto.core.settings.NormalSettings;
import org.bukkit.Bukkit;
import org.bukkit.Skin;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Profile {
	
	private UUID uuid;
	private String rank;

	private long chatCooldown = 0L;
	private int cps;
	private long lastClickTime = System.currentTimeMillis();
	private int clicks = 0;
	private boolean allowClick;

    private boolean frozen;
    private boolean likeNameMC;
    private UUID responsive;
    private ProfileState profileState;

    private ItemStack[] previousContents;
    private ItemStack[] previousArmor;

	private List<int[]> stats;
	public int[] settings;

	private boolean disguised;
	
	public Profile(final UUID uuid, final String rank) {
		this.uuid = uuid;
		this.rank = rank;
		this.frozen = false;
		this.cps = 0;
		this.allowClick = true;
		this.profileState = ProfileState.FREE;
		this.stats = Arrays.asList(new int[7], new int[7], new int[7]);
		this.settings = new int[10];
		for (int i = 0; i <= this.stats.get(2).length - 1; i++) this.stats.get(2)[i] = 1000;
		this.disguised = false;
	}

	public boolean isChatCooldownActive() {
		return this.chatCooldown > System.currentTimeMillis();
	}

	public long getChatCooldown() {
		return Math.max(0L, this.chatCooldown - System.currentTimeMillis());
	}

	public void applyChatCooldown(final int cooldown) {
		this.chatCooldown = System.currentTimeMillis() + cooldown * 1000L;
	}

	// Return whether the profile is in any of the given states
	public boolean isInState(ProfileState... states) {
		for (ProfileState state : states) {
			if (this.profileState.equals(state))
				return true;
		}
		return false;
	}
}

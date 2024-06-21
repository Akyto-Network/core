package gym.core.profile;

import java.util.UUID;

import org.bukkit.inventory.PlayerInventory;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Profile {
	
	private UUID uuid;
	private String rank;
	private long chatCooldown = 0L;
	private int cps;
	private boolean allowClick;
    private boolean frozen;
    private boolean likeNameMC;
    private UUID responsive;
    private ProfileStatus status;
    private PlayerInventory previousInventory;
	
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
	public boolean isInState(ProfileStatus... states) {
		for (ProfileStatus state : states) {
			if (this.status.equals(state))
				return true;
		}
		return false;
	}
	
	public Profile(final UUID uuid, final String rank) {
		this.uuid = uuid;
		this.rank = rank;
		this.frozen = false;
		this.cps = 0;
		this.allowClick = true;
	}

}

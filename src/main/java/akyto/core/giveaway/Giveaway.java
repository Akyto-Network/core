package akyto.core.giveaway;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Giveaway {

    private UUID creator;
    private GiveawayType type;
    private List<UUID> participants;

    public Giveaway(final UUID creator, final GiveawayType type) {
        this.creator = creator;
        this.type = type;
        this.participants = Lists.newArrayList();
    }
}

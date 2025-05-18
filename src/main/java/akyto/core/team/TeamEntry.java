package akyto.core.team;

import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamEntry {

    UUID leader;
    List<UUID> members;

    int [] stats = new int[2];

    public TeamEntry(final UUID leader) {
        this.leader = leader;
        this.members = Lists.newArrayList();
    }


}

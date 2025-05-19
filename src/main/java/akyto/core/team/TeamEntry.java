package akyto.core.team;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class TeamEntry {

    String name;
    UUID leader;
    List<UUID> members;

    int [] stats = new int[2];

    public TeamEntry(final String name, final UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = Lists.newArrayList();
    }
}

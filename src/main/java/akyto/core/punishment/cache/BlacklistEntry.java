package akyto.core.punishment.cache;

import lombok.Getter;

@Getter
public class BlacklistEntry {

    private final String ip;
    private final String reason;
    private final String judge;

    public BlacklistEntry(final String ip, final String reason, String judge) {
        this.ip = ip;
        this.reason = reason;
        this.judge = judge;
    }

}

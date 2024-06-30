package akyto.core.disguise;

import lombok.Getter;

@Getter
public class DisguiseEntry {

    final String name;
    final String dataSkin;
    final String signature;

    public DisguiseEntry(final String name, final String dataSkin, final String signature) {
        this.name = name;
        this.dataSkin = dataSkin;
        this.signature = signature;
    }
}

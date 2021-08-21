package eu.rex2go.chat2go.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Mute {

    @Getter
    private final long time;

    @Getter
    @Setter
    private long unmuteTime;

    @Getter
    private final String reason;

    @Getter
    private final UUID muter;


    public Mute(long time, long unmuteTime, String reason, UUID muter) {
        this.time = time;
        this.unmuteTime = unmuteTime;
        this.reason = reason;
        this.muter = muter;
    }
}

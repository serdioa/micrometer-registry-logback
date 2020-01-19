package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class LongTaskTimerSnapshot {

    @Getter
    protected final int activeTasks;

    @Getter
    protected final double duration;

    public LongTaskTimerSnapshot(LongTaskTimer longTaskTimer) {
        this.activeTasks = longTaskTimer.activeTasks();
        this.duration = longTaskTimer.duration(TimeUnit.NANOSECONDS);
    }
}

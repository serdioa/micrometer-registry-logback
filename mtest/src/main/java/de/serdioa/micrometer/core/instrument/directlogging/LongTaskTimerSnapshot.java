package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class LongTaskTimerSnapshot {

    @Getter
    protected final TimeUnit baseTimeUnit;

    @Getter
    protected final int activeTasks;

    @Getter
    protected final double duration;


    public LongTaskTimerSnapshot(LongTaskTimer longTaskTimer) {
        if (longTaskTimer instanceof ExtendedLongTaskTimer) {
            this.baseTimeUnit = ((ExtendedLongTaskTimer) longTaskTimer).baseTimeUnit();
        } else {
            this.baseTimeUnit = TimeUnit.NANOSECONDS;
        }

        this.activeTasks = longTaskTimer.activeTasks();
        this.duration = longTaskTimer.duration(this.baseTimeUnit);
    }
}

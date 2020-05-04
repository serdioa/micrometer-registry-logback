package de.serdioa.micrometer.logging.agg;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class LongTaskTimerSnapshot {

    @Getter
    protected final TimeUnit baseTimeUnit;

    @Getter
    protected final int activeTasks;

    @Getter
    protected final HistogramSnapshot histogramSnapshot;


    public LongTaskTimerSnapshot(LongTaskTimer longTaskTimer) {
        this.baseTimeUnit = longTaskTimer.baseTimeUnit();
        this.activeTasks = longTaskTimer.activeTasks();
        this.histogramSnapshot = longTaskTimer.takeSnapshot();
    }
}

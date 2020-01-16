package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class TimerSnapshot {

    @Getter
    protected final TimeUnit baseTimeUnit;

    @Getter
    protected final HistogramSnapshot histogramSnapshot;


    public TimerSnapshot(Timer timer) {
        this.baseTimeUnit = timer.baseTimeUnit();
        this.histogramSnapshot = timer.takeSnapshot();
    }
}

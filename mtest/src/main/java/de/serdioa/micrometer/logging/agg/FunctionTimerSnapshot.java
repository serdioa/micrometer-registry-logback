package de.serdioa.micrometer.logging.agg;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.FunctionTimer;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class FunctionTimerSnapshot {

    @Getter
    protected final TimeUnit baseTimeUnit;

    @Getter
    protected final long count;

    @Getter
    protected final double totalTime;


    public FunctionTimerSnapshot(FunctionTimer timer) {
        this.baseTimeUnit = timer.baseTimeUnit();
        this.count = (long) timer.count();
        this.totalTime = timer.totalTime(this.baseTimeUnit);
    }
}

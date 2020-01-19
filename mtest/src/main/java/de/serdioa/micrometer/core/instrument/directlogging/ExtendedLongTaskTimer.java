package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Statistic;


public interface ExtendedLongTaskTimer extends LongTaskTimer {

    TimeUnit baseTimeUnit();


    @Override
    default Iterable<Measurement> measure() {
        return Arrays.asList(
                new Measurement(() -> (double) this.activeTasks(), Statistic.ACTIVE_TASKS),
                new Measurement(() -> duration(this.baseTimeUnit()), Statistic.DURATION)
        );
    }
}

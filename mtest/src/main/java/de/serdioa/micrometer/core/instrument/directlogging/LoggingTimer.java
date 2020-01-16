package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepTimer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingTimer extends StepTimer {

    @Getter
    protected final Logger logger;

    @Getter
    protected final Marker tags;


    protected LoggingTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
            PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles,
            Logger logger, Marker tags) {
        super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepTimer;


class DirectLoggingTimer extends StepTimer {
    private final DirectMeterLogger logger;


    public DirectLoggingTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
                     PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles,
                     DirectMeterLogger logger) {
        super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles);

        this.logger = Objects.requireNonNull(logger);
    }


    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        super.recordNonNegative(amount, unit);
        this.logger.logTimer(this, TimeUnit.NANOSECONDS.convert(amount, unit));
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingDistributionSummary extends StepDistributionSummary {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    protected LoggingDistributionSummary(Meter.Id id, Clock clock,
            DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis,
            boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
        super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

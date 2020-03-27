package de.serdioa.micrometer.logging.base;

import java.util.Objects;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingDistributionSummary extends StepDistributionSummary implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    public LoggingDistributionSummary(Meter.Id id, Clock clock,
            DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis,
            boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
        super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

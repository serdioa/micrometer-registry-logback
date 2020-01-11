package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.step.StepTimer;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public abstract class AbstractLoggingMeterRegistry extends StepMeterRegistry {

    private final StepRegistryConfig config;


    protected AbstractLoggingMeterRegistry(StepRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);

        this.config().namingConvention(new LoggingJsonNamingConvention());
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    protected Logger getMeterLogger(Meter.Id meterId) {
        final String meterLoggerName = getMeterLoggerName(meterId);
        return LoggerFactory.getLogger(meterLoggerName);
    }


    private String getMeterLoggerName(Meter.Id meterId) {
        final String conventionName = this.getConventionName(meterId);
        final String prefix = this.config.prefix();
        return (prefix == null ? conventionName : prefix + "." + conventionName);
    }


    protected Marker getTags(Meter.Id meterId) {
        final List<Tag> conventionTags = getConventionTags(meterId);
        if (conventionTags.isEmpty()) {
            return null;
        } else {
            return conventionTags.stream()
                    .map(t -> Markers.append(t.getKey(), t.getValue()))
                    .reduce((first, second) -> first.and(second))
                    .orElse(null);
        }
    }


    protected abstract class AbstractLoggingTimer extends StepTimer {

        protected final Logger logger;
        protected final Marker tags;


        protected AbstractLoggingTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
                PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles,
                Logger logger, Marker tags) {
            super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles);

            this.logger = Objects.requireNonNull(logger);
            this.tags = tags;
        }
    }


    protected abstract class AbstractLoggingCounter extends StepCounter {

        private final Logger logger;
        private final Marker tags;


        protected AbstractLoggingCounter(Meter.Id id, Clock clock, long stepMillis, Logger logger, Marker tags) {
            super(id, clock, stepMillis);

            this.logger = Objects.requireNonNull(logger);
            this.tags = tags;
        }
    }


    private abstract class AbstractLoggingDistributionSummary extends StepDistributionSummary {

        private final Logger logger;
        private final Marker tags;


        protected AbstractLoggingDistributionSummary(Meter.Id id, Clock clock,
                DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis,
                boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
            super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles);

            this.logger = Objects.requireNonNull(logger);
            this.tags = tags;
        }
    }
}

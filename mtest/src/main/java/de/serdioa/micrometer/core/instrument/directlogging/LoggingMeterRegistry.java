package de.serdioa.micrometer.core.instrument.directlogging;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingMeterRegistry extends AbstractLoggingMeterRegistry {

    private final LoggingRegistryConfig config;


    public LoggingMeterRegistry() {
        this(LoggingRegistryConfig.DEFAULT);
    }


    public LoggingMeterRegistry(LoggingRegistryConfig config) {
        this(config, Clock.SYSTEM, new NamedThreadFactory("logging-metrics-publisher"));
    }


    private LoggingMeterRegistry(LoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock, threadFactory);
        this.config = Objects.requireNonNull(config);

        this.start(threadFactory);
    }


    @Override
    protected void publish() {
        System.out.println("LoggingMeterRegistry::publish() " + ZonedDateTime.now());
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingTimer(id, clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                this.config.step().toMillis(), true, logger, tags);
    }


    private class LoggingTimer extends AbstractLoggingTimer {

        public LoggingTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
                PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles,
                Logger logger, Marker tags) {
            super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles,
                    logger, tags);
        }
    }
}

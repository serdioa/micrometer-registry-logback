package de.serdioa.micrometer.logging.base;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public abstract class AbstractLoggingMeterRegistry extends StepMeterRegistry {

    private final StepRegistryConfig config;

    // Caches loggers for logging meters.
    private final ConcurrentMap<Meter.Id, Logger> loggerCache = new ConcurrentHashMap<>();

    // Caches log markers for logging meters.
    private final ConcurrentMap<Meter.Id, Optional<? extends Marker>> tagsCache = new ConcurrentHashMap<>();

    protected AbstractLoggingMeterRegistry(StepRegistryConfig config, Clock clock) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);

        this.config().namingConvention(new LoggingJsonNamingConvention());
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    protected Logger getMeterLogger(Meter.Id meterId) {
        return this.loggerCache.computeIfAbsent(meterId, this::buildMeterLogger);
    }


    private Logger buildMeterLogger(Meter.Id meterId) {
        final String conventionName = this.getConventionName(meterId);
        final String prefix = this.config.prefix();
        final String meterLoggerName = (prefix == null ? conventionName : prefix + "." + conventionName);

        return LoggerFactory.getLogger(meterLoggerName);
    }


    protected Marker getTags(Meter.Id meterId) {
        return this.tagsCache.computeIfAbsent(meterId, this::buildTags).orElse(null);
    }


    private Optional<? extends Marker> buildTags(Meter.Id meterId) {
        final List<Tag> conventionTags = getConventionTags(meterId);
        if (conventionTags.isEmpty()) {
            return Optional.empty();
        } else {
            return conventionTags.stream()
                    .map(t -> Markers.append(t.getKey(), t.getValue()))
                    .reduce((first, second) -> first.and(second));
        }
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
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
}

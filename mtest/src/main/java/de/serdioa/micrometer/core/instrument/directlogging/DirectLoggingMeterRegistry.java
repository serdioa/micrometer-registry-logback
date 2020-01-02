package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.Getter;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public class DirectLoggingMeterRegistry extends StepMeterRegistry {

    public static final String DEFAULT_PREFIX = "metrics";

    private final DirectLoggingRegistryConfig config;


    public DirectLoggingMeterRegistry() {
        this(DirectLoggingRegistryConfig.DEFAULT);
    }


    public DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config) {
        this(config, Clock.SYSTEM, new NamedThreadFactory("direct-logging-metrics-publisher"));
    }


    private DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);

        this.config().namingConvention(new LoggingJsonNamingConvention());
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    @Override
    protected void publish() {
        System.out.println("DirectLoggingMeterRegistry::publish()");
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new DirectLoggingTimer(id, clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                this.config.step().toMillis(), false);
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        return new DirectLoggingCounter(id, clock, config.step().toMillis());
    }


    private Logger getDirectLogger(Meter meter) {
        final String directLoggerName = getDirectLoggerName(meter);
        return LoggerFactory.getLogger(directLoggerName);
    }


    private String getDirectLoggerName(Meter meter) {
        final String conventionName = this.getConventionName(meter.getId());
        final String prefix = this.config.prefix();
        return (prefix == null ? conventionName : prefix + "." + conventionName);
    }


    private Marker printTags(Meter meter) {
        final List<Tag> conventionTags = getConventionTags(meter.getId());
        if (conventionTags.isEmpty()) {
            return null;
        } else {
            return conventionTags.stream()
                    .map(t -> Markers.append(t.getKey(), t.getValue()))
                    .reduce((first, second) -> first.and(second))
                    .orElse(null);
        }
    }


    private class DirectLoggingTimer extends StepTimer {

        private final Logger directLogger;
        private final Marker tags;


        public DirectLoggingTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
                PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles) {
            super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles);

            this.directLogger = getDirectLogger(this);
            this.tags = printTags(this);
        }


        @Override
        protected void recordNonNegative(long amount, TimeUnit unit) {
            super.recordNonNegative(amount, unit);

            if (this.directLogger.isInfoEnabled()) {
                long nanoseconds = unit.toNanos(amount);

                this.directLogger.info(this.tags, null, new TimerEvent(nanoseconds));
            }
        }
    }


    private static class TimerEvent implements StructuredArgument {

        @Getter
        private final long amount;


        public TimerEvent(long amount) {
            this.amount = amount;
        }


        @Override
        public String toString() {
            return "type=\"timer\",amt=" + this.amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("type", "timer");
            generator.writeNumberField("amt", this.amount);
        }
    }


    private class DirectLoggingCounter extends StepCounter {

        private final Logger directLogger;
        private final Marker tags;


        public DirectLoggingCounter(Id id, Clock clock, long stepMillis) {
            super(id, clock, stepMillis);

            this.directLogger = getDirectLogger(this);
            this.tags = printTags(this);
        }


        @Override
        public void increment(double amount) {
            super.increment(amount);

            if (this.directLogger.isInfoEnabled()) {
                this.directLogger.info(this.tags, null, new CounterEvent(amount, this.count()));
            }
        }
    }


    private static class CounterEvent implements StructuredArgument {

        @Getter
        private final double amount;

        @Getter
        private final double count;


        public CounterEvent(double amount, double count) {
            this.amount = amount;
            this.count = count;
        }


        @Override
        public String toString() {
            return "type=\"counter\",amt=" + this.amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("type", "counter");
            generator.writeNumberField("amt", this.amount);
        }
    }
}

package de.serdioa.micrometer.logging.direct;

import de.serdioa.micrometer.logging.base.LoggingJsonNamingConvention;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.MeterEquivalence;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.core.instrument.util.TimeUtils;
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


    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id,
            DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return new DirectLoggingDistributionSummary(id, clock, distributionStatisticConfig, scale,
                config.step().toMillis(), false);
    }


    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        return new DirectLoggingLongTaskTimer(id, this.clock);
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


    private class DirectLoggingDistributionSummary extends StepDistributionSummary {

        private final Logger directLogger;
        private final Marker tags;


        public DirectLoggingDistributionSummary(Id id, Clock clock,
                DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis,
                boolean supportsAggregablePercentiles) {
            super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles);

            this.directLogger = getDirectLogger(this);
            this.tags = printTags(this);
        }


        @Override
        protected void recordNonNegative(double amount) {
            super.recordNonNegative(amount);

            if (this.directLogger.isInfoEnabled()) {
                this.directLogger.info(this.tags, null, new DistributionSummaryEvent(amount));
            }
        }
    }


    private static class DistributionSummaryEvent implements StructuredArgument {

        @Getter
        private double amount;


        public DistributionSummaryEvent(double amount) {
            this.amount = amount;
        }


        @Override
        public String toString() {
            return "type=\"dsum\",amt=" + this.amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("type", "dsum");
            generator.writeNumberField("amt", this.amount);
        }
    }


    private class DirectLoggingLongTaskTimer extends AbstractMeter implements LongTaskTimer {

        private final ConcurrentMap<Long, Long> tasks = new ConcurrentHashMap<>();
        private final AtomicLong nextTask = new AtomicLong(0L);
        private final Clock clock;

        private final Logger directLogger;
        private final Marker tags;


        public DirectLoggingLongTaskTimer(Id id, Clock clock) {
            super(id);
            this.clock = clock;

            this.directLogger = getDirectLogger(this);
            this.tags = printTags(this);
        }


        @Override
        public Sample start() {
            long task = nextTask.getAndIncrement();
            tasks.put(task, clock.monotonicTime());

            if (this.directLogger.isInfoEnabled()) {
                this.directLogger.info(this.tags, null, new DirectLoggingLongTaskStartEvent(task));
            }

            return new Sample(this, task);
        }


        @Override
        public long stop(long task) {
            Long startTime = tasks.remove(task);
            long amount = (startTime != null ? clock.monotonicTime() - startTime : -1);

            if (this.directLogger.isInfoEnabled()) {
                this.directLogger.info(this.tags, null, new DirectLoggingLongTaskStopEvent(task, amount));
            }

            return amount;
        }


        @Override
        public double duration(long task, TimeUnit unit) {
            Long startTime = tasks.get(task);
            return (startTime != null) ? TimeUtils.nanosToUnit(clock.monotonicTime() - startTime, unit) : -1L;
        }


        @Override
        public double duration(TimeUnit unit) {
            long now = clock.monotonicTime();
            long sum = 0L;
            for (long startTime : tasks.values()) {
                sum += now - startTime;
            }
            return TimeUtils.nanosToUnit(sum, unit);
        }


        @Override
        public int activeTasks() {
            return tasks.size();
        }


        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            return MeterEquivalence.equals(this, o);
        }


        @Override
        public int hashCode() {
            return MeterEquivalence.hashCode(this);
        }
    }


    private static class DirectLoggingLongTaskStartEvent implements StructuredArgument {

        @Getter
        private final long task;


        public DirectLoggingLongTaskStartEvent(final long task) {
            this.task = task;
        }


        @Override
        public String toString() {
            return "type=\"ltask\",ev=\"start\",task=" + this.task;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("type", "ltask");
            generator.writeStringField("ev", "start");
            generator.writeNumberField("task", this.task);
        }
    }


    private static class DirectLoggingLongTaskStopEvent implements StructuredArgument {

        @Getter
        private final long task;

        @Getter
        private final long amount;


        public DirectLoggingLongTaskStopEvent(final long task, final long amount) {
            this.task = task;
            this.amount = amount;
        }


        @Override
        public String toString() {
            return "type=\"ltask\",ev=\"stop\",task=" + this.task + ",amt=" + this.amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("type", "ltask");
            generator.writeStringField("ev", "stop");
            generator.writeNumberField("task", this.task);
            generator.writeNumberField("amt", this.amount);
        }
    }
}

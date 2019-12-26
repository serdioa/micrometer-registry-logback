package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public class DirectLoggingMeterRegistry1 extends StepMeterRegistry {
    private static final Pattern PATTERN_EOL_CHARACTERS = Pattern.compile("[\n\r]");

    public static final String DEFAULT_PREFIX = "metrics";

    private final DirectLoggingRegistryConfig config;

    public DirectLoggingMeterRegistry1() {
        this(DirectLoggingRegistryConfig.DEFAULT);
    }


    public DirectLoggingMeterRegistry1(DirectLoggingRegistryConfig config) {
        this(config, Clock.SYSTEM, new NamedThreadFactory("direct-logging-metrics-publisher"));
    }


    private DirectLoggingMeterRegistry1(DirectLoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);

        config().namingConvention(NamingConvention.identity);
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


    private Logger getDirectLogger(Meter meter) {
        final String directLoggerName = getDirectLoggerName(meter);
        return LoggerFactory.getLogger(directLoggerName);
    }


    private String getDirectLoggerName(Meter meter) {
        final String conventionName = this.getConventionName(meter.getId());
        final String prefix = this.config.prefix();
        final String prefixedName = (prefix == null ? conventionName : prefix + "." + conventionName);

        return escape(prefixedName);
    }


    private Marker printTags(Meter meter) {
        final List<Tag> conventionTags = getConventionTags(meter.getId());
        if (conventionTags.isEmpty()) {
            return null;
        } else {
            return conventionTags.stream()
                .map(t -> Markers.append(escape(t.getKey()), escape(t.getValue())))
                .reduce((first, second) -> first.and(second))
                .orElse(null);
        }
    }


    private static String escape(String s) {
        return PATTERN_EOL_CHARACTERS.matcher(s).replaceAll("");
    }


    private static final StructuredArgument TIMER_TYPE = StructuredArguments.keyValue("type", "timer");
    private class DirectLoggingTimer extends StepTimer {

        // private final Logger directLogger;
        // private final Marker tags;

        public DirectLoggingTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
                     PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles) {
            super (id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles);

            // this.directLogger = getDirectLogger(this);
            // this.tags = printTags(this);
        }


        @Override
        protected void recordNonNegative(long amount, TimeUnit unit) {
            super.recordNonNegative(amount, unit);
            
            Logger directLogger = getDirectLogger(this);
            if (directLogger.isInfoEnabled()) {
                long nanoseconds = unit.toNanos(amount);

                Marker tags = printTags(this);
                StructuredArgument metrics = StructuredArguments.keyValue("amt", nanoseconds);
                directLogger.info(tags, "timer", metrics);
            }
        }
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.List;
import java.util.regex.Pattern;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Slf4jLogstashJsonMeterLogger implements DirectMeterLogger {
    private static final Pattern PATTERN_EOL_CHARACTERS = Pattern.compile("[\n\r]");

    public static final String DEFAULT_PREFIX = "metrics";


    private final String prefix;
    private final NamingConvention namingConventions;


    public Slf4jLogstashJsonMeterLogger() {
        this(DEFAULT_PREFIX);
    }


    public Slf4jLogstashJsonMeterLogger(final String prefix) {
        this(prefix, NamingConvention.identity);
    }


    public Slf4jLogstashJsonMeterLogger(final String prefix, final NamingConvention namingConventions) {
        this.prefix = prefix; // Null is allowed
        this.namingConventions = namingConventions;
    }


    @Override
    public void logTimer(Timer timer, long nanoseconds) {
        final Logger logger = getLogger(timer);
        if (logger.isInfoEnabled()) {
            final Marker tags = printTags(timer);
            final StructuredArgument metrics = StructuredArguments.keyValue("amt", nanoseconds);

            log(logger, "timer", tags, metrics);
        }
    }


    private void log(Logger logger, String type, Marker tags, StructuredArgument metrics) {
        logger.info(tags, type, metrics);
    }


    private Logger getLogger(Meter meter) {
        final String loggerName = getLoggerName(meter);
        return LoggerFactory.getLogger(loggerName);
    }


    private String getLoggerName(Meter meter) {
        final String conventionName = meter.getId().getConventionName(this.namingConventions);
        return escape(this.prefix == null ? conventionName : this.prefix + "." + conventionName);
    }


    private Marker printTags(Meter meter) {
        final List<Tag> conventionTags = meter.getId().getConventionTags(this.namingConventions);
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
}

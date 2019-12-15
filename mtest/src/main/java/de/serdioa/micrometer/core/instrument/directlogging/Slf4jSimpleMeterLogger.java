package de.serdioa.micrometer.core.instrument.directlogging;

import static java.util.stream.Collectors.joining;
import static net.logstash.logback.marker.Markers.append;

import java.util.List;
import java.util.regex.Pattern;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Slf4jSimpleMeterLogger implements DirectMeterLogger {
    private static final Pattern PATTERN_EOL_CHARACTERS = Pattern.compile("[\n\r]");

    public static final String DEFAULT_PREFIX = "metrics";


    private final String prefix;
    private final NamingConvention namingConventions;


    public Slf4jSimpleMeterLogger() {
        this(DEFAULT_PREFIX);
    }


    public Slf4jSimpleMeterLogger(final String prefix) {
        this(prefix, NamingConvention.identity);
    }


    public Slf4jSimpleMeterLogger(final String prefix, final NamingConvention namingConventions) {
        this.prefix = prefix; // Null is allowed
        this.namingConventions = namingConventions;
    }


    @Override
    public void logTimer(Timer timer, long nanoseconds) {
        final Logger logger = getLogger(timer);
        if (logger.isInfoEnabled()) {
            final String tags = printTags(timer);
            final String message = "amt=" + nanoseconds;

            log(logger, tags, message);
        }
    }


    private void log(Logger logger, String tags, String message) {
        // Use an explicit concatenation instead of varargs call to prevent creating a short-lived Object [].
        final StringBuilder sb = new StringBuilder();
        if (tags != null) {
            sb.append(tags).append(" ");
        }
        sb.append(message);

        logger.info(sb.toString());
    }


    private Logger getLogger(Meter meter) {
        final String loggerName = getLoggerName(meter);
        return LoggerFactory.getLogger(loggerName);
    }


    private String getLoggerName(Meter meter) {
        final String conventionName = meter.getId().getConventionName(this.namingConventions);
        return escape(this.prefix == null ? conventionName : this.prefix + "." + conventionName);
    }


    private String printTags(Meter meter) {
        final List<Tag> conventionTags = meter.getId().getConventionTags(this.namingConventions);
        if (conventionTags.isEmpty()) {
            return null;
        } else {
            final String tags = conventionTags.stream()
                .map(t -> t.getKey() + "=" + t.getValue())
                .collect(joining(",", "{", "}"));
            return escape(tags);
        }
    }


    private static String escape(String s) {
        return PATTERN_EOL_CHARACTERS.matcher(s).replaceAll("");
    }
}

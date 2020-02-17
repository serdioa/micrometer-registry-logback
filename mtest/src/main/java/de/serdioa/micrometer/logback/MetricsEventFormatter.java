package de.serdioa.micrometer.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.ContextAware;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventCompositeJsonFormatter;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;


public class MetricsEventFormatter extends LoggingEventCompositeJsonFormatter {

    public static final String FIELD_TIMESTAMP = "ts";
    public static final String FIELD_METRIC = "metric";
    public static final String FIELD_VALUES = "val";


    public MetricsEventFormatter(ContextAware declaredOrigin) {
        super(declaredOrigin);

        JsonProviders<ILoggingEvent> providers = new JsonProviders<>();

        // Timestamp
        final LoggingEventFormattedTimestampJsonProvider timestampProvider = new LoggingEventFormattedTimestampJsonProvider();
        timestampProvider.setFieldName(FIELD_TIMESTAMP);
        providers.addProvider(timestampProvider);

        // Name of the metric
        final LoggerNameJsonProvider nameProvider = new LoggerNameJsonProvider();
        nameProvider.setFieldName(FIELD_METRIC);
        providers.addProvider(nameProvider);

        // Markers (tags)
        providers.addProvider(new LogstashMarkersJsonProvider());

        // Values of the metric
        final ArgumentsJsonProvider valuesProvider = new ArgumentsJsonProvider();
        valuesProvider.setFieldName(FIELD_VALUES);
        providers.addProvider(valuesProvider);

        this.setProviders(providers);
    }

}

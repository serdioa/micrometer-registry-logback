package de.serdioa.micrometer.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.composite.CompositeJsonFormatter;
import net.logstash.logback.encoder.CompositeJsonEncoder;


public class MetricsEventEncoder extends CompositeJsonEncoder<ILoggingEvent> {

    @Override
    protected CompositeJsonFormatter<ILoggingEvent> createFormatter() {
        return new MetricsEventFormatter(this);
    }
}

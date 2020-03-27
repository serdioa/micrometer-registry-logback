package de.serdioa.micrometer.logging.base;

import java.util.Objects;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.StringEscapeUtils;


public class LoggingJsonNamingConvention implements NamingConvention {

    private final NamingConvention delegate;


    public LoggingJsonNamingConvention() {
        this(NamingConvention.dot);
    }


    public LoggingJsonNamingConvention(NamingConvention delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }


    @Override
    public String name(String name, Meter.Type type, String baseUnit) {
        return this.escape(this.delegate.name(name, type, baseUnit));
    }


    @Override
    public String tagKey(String key) {
        return this.escape(this.delegate.tagKey(key));
    }


    @Override
    public String tagValue(String value) {
        return this.escape(this.delegate.tagValue(value));
    }


    private String escape(String str) {
        return StringEscapeUtils.escapeJson(str);
    }
}

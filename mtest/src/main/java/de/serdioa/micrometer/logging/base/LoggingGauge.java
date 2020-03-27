package de.serdioa.micrometer.logging.base;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.internal.DefaultGauge;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingGauge<T> extends DefaultGauge<T> implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    public LoggingGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction, Logger logger, Marker tags) {
        super(id, obj, valueFunction);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepFunctionCounter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingFunctionCounter<T> extends StepFunctionCounter<T> implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    protected LoggingFunctionCounter(Id id, Clock clock, long stepMillis, T obj, ToDoubleFunction<T> f,
            Logger logger, Marker tags) {
        super(id, clock, stepMillis, obj, f);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

package de.serdioa.micrometer.logging.base;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepFunctionTimer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingFunctionTimer<T> extends StepFunctionTimer<T> implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    public LoggingFunctionTimer(Id id, Clock clock, long stepMillis, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit baseTimeUnit,
            Logger logger, Marker tags) {
        super(id, clock, stepMillis, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, baseTimeUnit);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

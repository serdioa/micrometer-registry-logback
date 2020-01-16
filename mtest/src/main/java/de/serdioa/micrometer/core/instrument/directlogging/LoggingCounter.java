package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepCounter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingCounter extends StepCounter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    protected LoggingCounter(Meter.Id id, Clock clock, long stepMillis, Logger logger, Marker tags) {
        super(id, clock, stepMillis);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

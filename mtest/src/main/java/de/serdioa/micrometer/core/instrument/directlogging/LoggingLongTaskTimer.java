package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingLongTaskTimer extends DefaultLongTaskTimer implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    public LoggingLongTaskTimer(Id id, Clock clock, Logger logger, Marker tags) {
        super(id, clock);

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

package de.serdioa.micrometer.logging.base;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingLongTaskTimer extends DefaultExtendedLongTaskTimer implements LoggingMeter {

    @Getter
    private final Logger logger;

    @Getter
    private final Marker tags;


    public LoggingLongTaskTimer(Id id, Clock clock, TimeUnit baseTimeUnit, Logger logger, Marker tags) {
        super(id, clock, baseTimeUnit);
        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }
}

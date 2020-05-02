package de.serdioa.micrometer.logging.base;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;


public class DefaultExtendedLongTaskTimer extends DefaultLongTaskTimer implements ExtendedLongTaskTimer {

    private final TimeUnit baseTimeUnit;


    public DefaultExtendedLongTaskTimer(Id id, Clock clock, TimeUnit baseTimeUnit) {
        super(id, clock);

        this.baseTimeUnit = Objects.requireNonNull(baseTimeUnit);
    }


    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }
}

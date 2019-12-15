package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.Timer;


public interface DirectMeterLogger {
    void logTimer(Timer timer, long nanoseconds);
}

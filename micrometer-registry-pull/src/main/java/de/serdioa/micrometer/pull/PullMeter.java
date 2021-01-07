package de.serdioa.micrometer.pull;

import io.micrometer.core.instrument.Meter;


public interface PullMeter extends Meter {
    Iterable<PullMeasurement> getPullMeasurements();
}

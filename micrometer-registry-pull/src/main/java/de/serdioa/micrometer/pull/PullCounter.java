package de.serdioa.micrometer.pull;

import java.util.Collections;
import java.util.List;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepCounter;


public class PullCounter extends StepCounter implements PullMeter {

    public static final String COUNT = "count";

    private final List<PullMeasurement> measurements = Collections.singletonList(
            new PullMeasurement(COUNT, this::count));


    public PullCounter(Id id, Clock clock, long stepMillis) {
        super(id, clock, stepMillis);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

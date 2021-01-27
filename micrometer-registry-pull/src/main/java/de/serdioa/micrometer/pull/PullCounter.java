package de.serdioa.micrometer.pull;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepCounter;


public class PullCounter extends StepCounter implements PullMeter {

    private final Iterable<PullMeasurement> measurements;


    public PullCounter(Id id, Clock clock, long stepMillis) {
        super(id, clock, stepMillis);

        this.measurements = PullMeterUtil.measurements(
                PullMeasurement.of(id.getType(), PullMeasurement.Type.COUNT, this::count)
        );
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

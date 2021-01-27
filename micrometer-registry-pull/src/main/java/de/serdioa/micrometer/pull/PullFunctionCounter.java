package de.serdioa.micrometer.pull;

import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepFunctionCounter;


public class PullFunctionCounter<T> extends StepFunctionCounter<T> implements PullMeter {

    private final Iterable<PullMeasurement> measurements;


    public PullFunctionCounter(Id id, Clock clock, long stepMillis, T obj, ToDoubleFunction<T> f) {
        super(id, clock, stepMillis, obj, f);

        this.measurements = PullMeterUtil.measurements(
                PullMeasurement.of(id.getType(), PullMeasurement.Type.COUNT, this::count)
        );
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

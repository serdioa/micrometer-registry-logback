package de.serdioa.micrometer.pull;

import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepFunctionCounter;


public class PullFunctionCounter<T> extends StepFunctionCounter<T> implements PullMeter {

    private final List<PullMeasurement> measurements = Collections.singletonList(
            new PullMeasurement("count", this::count));


    public PullFunctionCounter(Id id, Clock clock, long stepMillis, T obj, ToDoubleFunction<T> f) {
        super(id, clock, stepMillis, obj, f);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

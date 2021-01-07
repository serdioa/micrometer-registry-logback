package de.serdioa.micrometer.pull;

import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.internal.DefaultGauge;


public class PullGauge<T> extends DefaultGauge<T> implements PullMeter {

    private final List<PullMeasurement> measurements = Collections.singletonList(
            new PullMeasurement("value", this::value));


    public PullGauge(Id id, T obj, ToDoubleFunction<T> value) {
        super(id, obj, value);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

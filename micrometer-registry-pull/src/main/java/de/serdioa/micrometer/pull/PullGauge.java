package de.serdioa.micrometer.pull;

import java.util.Collections;
import java.util.List;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.internal.DefaultGauge;


public class PullGauge<T> extends DefaultGauge<T> implements PullMeter {

    public static final String VALUE = "value";

    private final List<PullMeasurement> measurements = Collections.singletonList(
            new PullMeasurement(VALUE, this::value));


    public PullGauge(Id id, T obj, ToDoubleFunction<T> value) {
        super(id, obj, value);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

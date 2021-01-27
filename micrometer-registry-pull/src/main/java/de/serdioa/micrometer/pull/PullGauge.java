package de.serdioa.micrometer.pull;

import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.internal.DefaultGauge;


public class PullGauge<T> extends DefaultGauge<T> implements PullMeter {

    private final Iterable<PullMeasurement> measurements;


    public PullGauge(Id id, T obj, ToDoubleFunction<T> value) {
        super(id, obj, value);

        this.measurements = PullMeterUtil.measurements(
                PullMeasurement.of(id.getType(), PullMeasurement.Type.VALUE, this::value)
        );
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

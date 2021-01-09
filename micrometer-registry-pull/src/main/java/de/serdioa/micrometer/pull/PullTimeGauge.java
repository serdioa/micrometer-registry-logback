package de.serdioa.micrometer.pull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.util.TimeUtils;


public class PullTimeGauge<T> extends DefaultGauge<T> implements TimeGauge, PullMeter {

    public static final String VALUE = "value";

    // The time unit in which the valueFunction provides the time.
    private final TimeUnit valueFunctionUnit;

    // The time unit in which this gauge provides the time by default.
    private final TimeUnit baseTimeUnit;

    private final List<PullMeasurement> measurements = Collections.singletonList(
            new PullMeasurement(VALUE, this::value));


    public PullTimeGauge(Meter.Id id, T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction,
            TimeUnit baseTimeUnit) {
        super(id, obj, valueFunction);

        this.valueFunctionUnit = Objects.requireNonNull(valueFunctionUnit);
        this.baseTimeUnit = Objects.requireNonNull(baseTimeUnit);
    }


    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }


    @Override
    public double value() {
        // Get the value from the underlying function, expressed in this.valueFunctionUnit.
        double valueInFunctionUnit = super.value();

        // Convert the value to the output time unit.
        return TimeUtils.convert(valueInFunctionUnit, this.valueFunctionUnit, this.baseTimeUnit);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

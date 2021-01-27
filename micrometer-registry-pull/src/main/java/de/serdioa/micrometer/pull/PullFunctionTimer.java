package de.serdioa.micrometer.pull;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepFunctionTimer;


public class PullFunctionTimer<T> extends StepFunctionTimer<T> implements PullMeter {

    private final Iterable<PullMeasurement> measurements;


    public PullFunctionTimer(Meter.Id id, Clock clock, long stepMillis, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit baseTimeUnit) {
        super(id, clock, stepMillis, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, baseTimeUnit);

        this.measurements = PullMeterUtil.measurements(
                PullMeasurement.of(id.getType(), PullMeasurement.Type.COUNT, this::count),
                PullMeasurement.of(id.getType(), PullMeasurement.Type.TOTAL_TIME, this::totalTime),
                PullMeasurement.of(id.getType(), PullMeasurement.Type.MEAN, this::mean)
        );
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }


    private double totalTime() {
        return this.totalTime(this.baseTimeUnit());
    }


    private double mean() {
        return this.mean(this.baseTimeUnit());
    }
}

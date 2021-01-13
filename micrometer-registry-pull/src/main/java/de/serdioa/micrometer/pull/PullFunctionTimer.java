package de.serdioa.micrometer.pull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepFunctionTimer;


public class PullFunctionTimer<T> extends StepFunctionTimer<T> implements PullMeter {

    public static final String COUNT = "count";
    public static final String TOTAL_TIME = "totalTime";
    public static final String MEAN = "mean";

    private final List<PullMeasurement> measurements;


    public PullFunctionTimer(Meter.Id id, Clock clock, long stepMillis, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit baseTimeUnit) {
        super(id, clock, stepMillis, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, baseTimeUnit);

        this.measurements = Arrays.asList(
                new PullMeasurement(COUNT, this::count),
                new PullMeasurement(TOTAL_TIME, this::totalTime),
                new PullMeasurement(MEAN, this::mean));
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

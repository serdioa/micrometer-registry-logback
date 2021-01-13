package de.serdioa.micrometer.pull;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.lang.Nullable;


public abstract class PullMeterRegistry extends MeterRegistry {

    protected final PullConfig pullConfig;


    protected PullMeterRegistry(PullConfig config, Clock clock) {
        super(clock);

        config.requireValid();
        this.pullConfig = config;
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder()
                .expiry(this.pullConfig.pollingFrequency())
                .build()
                .merge(DistributionStatisticConfig.DEFAULT);
    }


    @Override
    protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction) {
        return new PullGauge<>(id, obj, valueFunction);
    }


    @Override
    protected <T> TimeGauge newTimeGauge(Meter.Id id, @Nullable T obj, TimeUnit valueFunctionUnit,
            ToDoubleFunction<T> valueFunction) {
        return new PullTimeGauge<>(id, obj, valueFunctionUnit, valueFunction, this.getBaseTimeUnit());
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        return new PullCounter(id, this.clock, this.pullConfig.pollingFrequency().toMillis());
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return new PullDistributionSummary(id, this.clock, distributionStatisticConfig, scale, this.pullConfig
                .pollingFrequency().toMillis(), false);
    }


    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new PullFunctionTimer<>(id, this.clock, this.pullConfig.pollingFrequency().toMillis(), obj, countFunction,
                totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit());
    }


    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        return new PullFunctionCounter<>(id, this.clock, this.pullConfig.pollingFrequency().toMillis(), obj, countFunction);
    }
}

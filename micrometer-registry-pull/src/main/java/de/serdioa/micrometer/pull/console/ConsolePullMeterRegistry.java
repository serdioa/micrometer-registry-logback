package de.serdioa.micrometer.pull.console;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import de.serdioa.micrometer.pull.PullMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;


/**
 * A simple implementation of a pulling meter registry which writes metrics to the console. This implementation is
 * primarily intended for testing purposes.
 */
public class ConsolePullMeterRegistry extends PullMeterRegistry {

    // @GuardedBy(this.executorLock)
    private ScheduledExecutorService executor;
    private final Object executorLock = new Object();


    public ConsolePullMeterRegistry(ConsolePullConfig config, Clock clock) {
        super(config, clock);

        this.startExecutor();
    }


    @Override
    public void close() {
        this.stopExecutor();
        super.close();
    }


    private void startExecutor() {
        synchronized (this.executorLock) {
            if (this.executor == null) {
                this.executor = Executors.newSingleThreadScheduledExecutor();

                long step = this.pullConfig.pollingFrequency().toMillis();
                this.executor.scheduleAtFixedRate(this::publish, step, step, TimeUnit.MILLISECONDS);
            }
        }
    }


    private void stopExecutor() {
        synchronized (this.executorLock) {
            if (this.executor != null) {
                this.executor.shutdown();
                this.executor = null;
            }
        }
    }


    private void publish() {
        System.out.println("ConsolePullMeterRegistry.publish()");
    }


    @Override
    protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction) {
        return new DefaultGauge(id, obj, valueFunction);
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

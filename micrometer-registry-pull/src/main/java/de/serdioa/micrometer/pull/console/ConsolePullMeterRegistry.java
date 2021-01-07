package de.serdioa.micrometer.pull.console;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.serdioa.micrometer.pull.PullMeasurement;
import de.serdioa.micrometer.pull.PullMeter;
import de.serdioa.micrometer.pull.PullMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;


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
        this.getMeters().stream().forEach(this::publishMeter);
    }


    private void publishMeter(Meter meter) {
        Meter.Id id = meter.getId();
        PullMeter pullMeter = (PullMeter) meter;
        for (PullMeasurement m : pullMeter.getPullMeasurements()) {
            System.out.println(id.getName() + ": " + m);
        }
    }
}

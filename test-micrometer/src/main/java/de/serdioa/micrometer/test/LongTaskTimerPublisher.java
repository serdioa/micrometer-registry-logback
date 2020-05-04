package de.serdioa.micrometer.test;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;


public class LongTaskTimerPublisher extends AbstractMetricsPublisher {

    private final LongTaskTimer timer;
    private final Random rnd = new Random();
    private final ScheduledExecutorService executor;


    public LongTaskTimerPublisher(MeterRegistry registry) {
        super(registry);

        this.timer = LongTaskTimer.builder("publisher.longTaskTimer")
                .publishPercentiles(0.5, 0.75, 0.9, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(10))
                .maximumExpectedValue(Duration.ofMillis(150))
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.registry);

        this.executor = Executors.newScheduledThreadPool(1);
    }


    @Override
    public void stop() throws InterruptedException {
        super.stop();

        this.executor.shutdown();
    }


    @Override
    protected void publish() throws InterruptedException {
        // Create a new long-running task.
        LongTaskTimer.Sample task = this.timer.start();

        // Simulate a long-running task: schedule it to stop after a random time.
        long taskDuration = time(50, 10);
        this.executor.schedule(() -> task.stop(), taskDuration, TimeUnit.MILLISECONDS);

        // Sleep before starting the next task.
        long sleepDuration = time(10, 10);
        Thread.sleep(sleepDuration);
    }


    private long time(long mean, long stddev) {
        final double value = mean + this.rnd.nextGaussian() * stddev;
        return (long) (value < 0 ? 0 : value);
    }
}

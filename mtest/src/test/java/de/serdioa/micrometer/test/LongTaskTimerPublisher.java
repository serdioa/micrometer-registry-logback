package de.serdioa.micrometer.test;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;


public class LongTaskTimerPublisher implements Runnable {

    private final MeterRegistry meterRegistry;
    private final LongTaskTimer timer;
    private final Random rnd = new Random();

    private ScheduledExecutorService executor;


    public LongTaskTimerPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        this.timer = LongTaskTimer.builder("publisher.longTaskTimer")
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("CounterPublisher has been started");
        this.executor = Executors.newScheduledThreadPool(1);
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("CounterPublisher has been interrupted");
            this.executor.shutdown();
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
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

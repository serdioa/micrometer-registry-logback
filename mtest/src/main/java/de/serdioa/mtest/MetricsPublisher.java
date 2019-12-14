package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


public class MetricsPublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final Timer timer;
    private final Random rnd = new Random();

    public MetricsPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.timer = this.meterRegistry.timer("publisher.timer");
    }


    @Override
    public void run() {
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("Publisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        final long startTs = System.nanoTime();
        doWork();
        final long endTs = System.nanoTime();
        final long duration = endTs - startTs;

        this.timer.record(duration, TimeUnit.NANOSECONDS);
//        System.out.println("Publisher: " + duration);
    }


    private void doWork() throws InterruptedException {
        final double gauss = this.rnd.nextGaussian();
        long sleep = (long) (gauss * 10 + 100);
        sleep = (sleep > 0 ? sleep : 0);

        Thread.sleep(sleep);
    }
}

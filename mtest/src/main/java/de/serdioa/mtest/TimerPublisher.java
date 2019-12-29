package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


public class TimerPublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final Timer timer;
    private final Random rnd = new Random();

    public TimerPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        this.timer = Timer.builder("publisher.timer")
                .tags("t.\n1", "v.\n1", "t.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("TimerPublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("TimerPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        final long startTs = System.nanoTime();
        doWork();
        final long endTs = System.nanoTime();
        final long duration = endTs - startTs;

        this.timer.record(duration, TimeUnit.NANOSECONDS);
    }


    private void doWork() throws InterruptedException {
        final double gauss = this.rnd.nextGaussian();
        long sleep = (long) (gauss * 10 + 100);
        sleep = (sleep > 0 ? sleep : 0);

        Thread.sleep(sleep);
    }
}

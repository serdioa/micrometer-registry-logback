package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


public class CounterPublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final Counter counter;
    private final Random rnd = new Random();

    public CounterPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        this.counter = Counter.builder("publisher.counter")
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("CounterPublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("CounterPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        int increment = doWork();
        this.counter.increment(increment);
    }


    private int doWork() throws InterruptedException {
        final double gauss = this.rnd.nextGaussian();
        long sleep = (long) (gauss * 10 + 10);
        sleep = (sleep > 0 ? sleep : 0);

        Thread.sleep(sleep);

        return this.rnd.nextInt(10);
    }
}

package de.serdioa.micrometer.test;

import java.util.Random;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


public class CounterPublisher extends AbstractMetricsPublisher {

    private final Counter counter;
    private final Random rnd = new Random();


    public CounterPublisher(MeterRegistry registry) {
        super(registry);

        this.counter = Counter.builder("publisher.counter")
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        final long sleep = Math.max(0, (long) (this.rnd.nextGaussian() * 10 + 10));
        Thread.sleep(sleep);

        final int count = this.rnd.nextInt(10);
        this.counter.increment(count);
    }
}

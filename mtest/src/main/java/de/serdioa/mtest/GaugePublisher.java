package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.apache.commons.lang3.mutable.MutableDouble;


public class GaugePublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final MutableDouble gauge = new MutableDouble();
    private final Random rnd = new Random();

    public GaugePublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        this.meterRegistry.gauge("publisher.gauge", Tags.of("g.\n1", "v.\n2").and("g.2", "v.2"), this.gauge);
    }


    @Override
    public void run() {
        System.out.println("GaugePublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("GaugePublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        double value = this.rnd.nextGaussian();
        this.gauge.setValue(value);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }
}

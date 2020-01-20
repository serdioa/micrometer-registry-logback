package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.mutable.MutableDouble;


public class FunctionCounterPublisher implements Runnable {

    private final MeterRegistry meterRegistry;
    private final MutableDouble value = new MutableDouble();
    private final Random rnd = new Random();


    public FunctionCounterPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        FunctionCounter.builder("publisher.functionCounter", this.value, Number::doubleValue)
                .tags("g.\n1", "v.\n1", "g.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("FunctionCounterPublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("FunctionCounterPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        double increment = Math.abs(this.rnd.nextGaussian());
        this.value.add(increment);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }
}

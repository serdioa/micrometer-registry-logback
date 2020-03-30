package de.serdioa.micrometer.test;

import java.util.Random;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.mutable.MutableDouble;


public class FunctionCounterPublisher extends AbstractMetricsPublisher {

    private final MutableDouble value = new MutableDouble();
    private final Random rnd = new Random();


    public FunctionCounterPublisher(MeterRegistry registry) {
        super(registry);

        FunctionCounter.builder("publisher.functionCounter", this.value, Number::doubleValue)
                .tags("g.\n1", "v.\n1", "g.2", "v.2")
                .register(this.registry);

    }


    @Override
    protected void publish() throws InterruptedException {
        double increment = Math.abs(this.rnd.nextGaussian());
        this.value.add(increment);

        long sleep = (long) (this.rnd.nextInt(100));
        Thread.sleep(sleep);
    }
}

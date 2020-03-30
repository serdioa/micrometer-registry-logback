package de.serdioa.micrometer.test;

import java.util.Random;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.mutable.MutableDouble;


public class GaugePublisher extends AbstractMetricsPublisher {

    private final MutableDouble gauge = new MutableDouble();
    private final Random rnd = new Random();


    public GaugePublisher(MeterRegistry registry) {
        super(registry);

        Gauge.builder("publisher.gauge", this.gauge, Number::doubleValue)
                .baseUnit("milliseconds")
                .tags("g.\n1", "v.\n1", "g.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        double value = this.rnd.nextGaussian();
        this.gauge.setValue(value);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }
}

package de.serdioa.micrometer.test;

import java.util.Random;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;


public class DistributionSummaryPublisher extends AbstractMetricsPublisher {

    private final DistributionSummary ds;
    private final Random rnd = new Random();


    public DistributionSummaryPublisher(MeterRegistry registry) {
        super(registry);

        this.ds = DistributionSummary.builder("publisher.ds")
                .publishPercentiles(0.5, 0.75, 0.9, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(1L)
                .maximumExpectedValue(50L)
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        final long sleep = Math.max(0, (long) (this.rnd.nextGaussian() * 10 + 10));
        Thread.sleep(sleep);

        this.ds.record(sleep);
    }
}

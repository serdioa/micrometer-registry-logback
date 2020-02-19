package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;


public class DistributionSummaryPublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final DistributionSummary ds;
    private final Random rnd = new Random();

    public DistributionSummaryPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        this.ds = DistributionSummary.builder("publisher.ds")
                .publishPercentiles(0.5, 0.75, 0.9, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(1L)
                .maximumExpectedValue(50L)
                .tags("c.\n1", "v.\n1", "c.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("DistributionSummaryPublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("DistributionSummaryPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        double amount = doWork();
        this.ds.record(amount);
    }


    private double doWork() throws InterruptedException {
        final double gauss = this.rnd.nextGaussian();
        long sleep = (long) (gauss * 10 + 10);
        sleep = (sleep > 0 ? sleep : 0);

        Thread.sleep(sleep);

        return Math.max(0.0, this.rnd.nextGaussian() + 10.0);
    }
}

package de.serdioa.micrometer.test;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


public class TimerPublisher extends AbstractMetricsPublisher {

    private final Timer timer;
    private final Random rnd = new Random();


    public TimerPublisher(MeterRegistry registry) {
        super(registry);

        this.timer = Timer.builder("publisher.timer")
                .publishPercentiles(0.5, 0.75, 0.9, 0.95)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(50))
                .maximumExpectedValue(Duration.ofMillis(150))
                .tags("t.\n1", "v.\n1", "t.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        final long startTs = System.nanoTime();
        doWork();
        final long endTs = System.nanoTime();
        final long duration = endTs - startTs;

        this.timer.record(duration, TimeUnit.NANOSECONDS);
    }


    private void doWork() throws InterruptedException {
        final long sleep = Math.max(0, (long) (this.rnd.nextGaussian() * 10 + 100));
        Thread.sleep(sleep);
    }
}

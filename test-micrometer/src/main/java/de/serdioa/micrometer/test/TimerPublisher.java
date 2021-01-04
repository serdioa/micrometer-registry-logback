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
                .sla(Duration.ofMillis(9900),
                        Duration.ofMillis(9910),
                        Duration.ofMillis(9920),
                        Duration.ofMillis(9930),
                        Duration.ofMillis(9940),
                        Duration.ofMillis(9950),
                        Duration.ofMillis(9960),
                        Duration.ofMillis(9970),
                        Duration.ofMillis(9980),
                        Duration.ofMillis(9990),
                        Duration.ofMillis(10000))
                .tags("t.\n1", "v.\n1", "t.2", "v.2")
                .distributionStatisticExpiry(Duration.ofSeconds(10))
                .distributionStatisticBufferLength(2)
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        for (int i = 10000; i > 0; --i) {
            this.record(i);
            Thread.sleep(1000);
        }
    }


    private void record(int millis) {
        System.out.println("Recording: " + millis + " ms");
        this.timer.record(millis, TimeUnit.MILLISECONDS);
    }


    private void doWork() throws InterruptedException {
        final long sleep = Math.max(0, (long) (this.rnd.nextGaussian() * 10 + 100));
        Thread.sleep(sleep);
    }
}

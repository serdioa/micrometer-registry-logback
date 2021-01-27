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
                .tags("t.\n1", "v.\n1", "t.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        for (int i = 1000; i > 0; i -= 10) {
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

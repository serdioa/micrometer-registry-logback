package de.serdioa.micrometer.test;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import org.apache.commons.lang3.mutable.MutableLong;


public class TimeGaugePublisher implements Runnable {
    private final MeterRegistry meterRegistry;
    private final MutableLong timeGauge = new MutableLong();
    private final Random rnd = new Random();

    public TimeGaugePublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        TimeGauge.builder("publisher.timeGauge", this.timeGauge, TimeUnit.SECONDS, Number::doubleValue)
                .tags("tg.\n1", "v.\n1", "tg.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("TimeGaugePublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("TimeGaugePublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        int value = this.rnd.nextInt(10);
        this.timeGauge.setValue(value);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }
}

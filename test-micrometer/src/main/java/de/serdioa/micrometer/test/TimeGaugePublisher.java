package de.serdioa.micrometer.test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import org.apache.commons.lang3.mutable.MutableLong;


public class TimeGaugePublisher extends AbstractMetricsPublisher {

    private final MutableLong timeGauge = new MutableLong();
    private final Random rnd = new Random();


    public TimeGaugePublisher(MeterRegistry registry) {
        super(registry);

        TimeGauge.builder("publisher.timeGauge", this.timeGauge, TimeUnit.SECONDS, Number::doubleValue)
                .tags("tg.\n1", "v.\n1", "tg.2", "v.2")
                .register(this.registry);
    }


    @Override
    protected void publish() throws InterruptedException {
        double value = this.rnd.nextInt(10);
        this.timeGauge.setValue(value);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }
}

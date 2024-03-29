package de.serdioa.micrometer.test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;


public class ManagedMicrometerTest {

    private MockClock clock;
    private MeterRegistry meterRegistry;
    private Timer timer;

    private AtomicLong timeGaugeSource = new AtomicLong();
    private TimeGauge timeGauge;


    private void run() {
        this.setup();
        // this.testTimer();
        this.testTimeGauge();
    }


    private void setup() {
        this.clock = new MockClock();

        SimpleConfig registryConfig = k -> {
            if (k.endsWith("step")) {
                return "10s";
//                return null;
            } else if (k.endsWith("mode")) {
                return "STEP";
            } else {
                return null;
            }
        };

        double[] sla = new double[10];
        for (int i = 0; i < sla.length; ++i) {
            sla[i] = TimeUnit.MILLISECONDS.toNanos(100 + i * 100);
        }

        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(registryConfig, this.clock);
        MeterFilter filter = new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)
                        .percentiles(0.5, 0.75, 0.9, 0.95, 1.0)
                        .serviceLevelObjectives(sla)
                        .expiry(Duration.ofSeconds(10))
                        .bufferLength(4)
                        .build().merge(config);
            }
        };
        meterRegistry.config().meterFilter(filter);

        this.meterRegistry = meterRegistry;

        this.timer = Timer.builder("publisher.timer").register(this.meterRegistry);

        this.timeGauge = TimeGauge.builder("publisher.timeGauge", this.timeGaugeSource, TimeUnit.MINUTES,
                AtomicLong::doubleValue).register(meterRegistry);
    }


    private void testTimeGauge() {
        this.timeGaugeSource.set(5);
        System.out.println("value=" + this.timeGauge.value());
        System.out.println("value(min)=" + this.timeGauge.value(TimeUnit.MINUTES));
        System.out.println("value(sec)=" + this.timeGauge.value(TimeUnit.SECONDS));
        System.out.println("value(ms)=" + this.timeGauge.value(TimeUnit.MILLISECONDS));

        System.out.println("measurements:");
        for (Measurement m : this.timeGauge.measure()) {
            System.out.println(m.getStatistic().name() + ": " + m.getValue());
        }
    }


    private void testTimer() {
        this.print();

        for (int i = 0; i < 50; ++i) {
            for (int j = 0; j < 10; ++j) {
                this.addTimer(500);
            }
            this.print();
            this.waitTime(2500);
        }
    }


    private void addTimer(long millis) {
        this.timer.record(millis, TimeUnit.MILLISECONDS);
    }


    private void waitTime(long millis) {
        this.clock.add(millis, TimeUnit.MILLISECONDS);
    }


    private void print() {
        StringBuilder sb = new StringBuilder();
        MetricsFormatter.format(this.timer, sb);
        System.out.println("Time=" + this.clock.wallTime());
        System.out.println(sb);
    }


    public static void main(String[] args) {
        new ManagedMicrometerTest().run();
    }
}

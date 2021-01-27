package de.serdioa.micrometer.test;


import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;


public class StandaloneMicrometerTest {

    public static void main(String[] args) {
        SimpleConfig registryConfig = k -> {
            if (k.endsWith("step")) {
                return "10s";
            } else if (k.endsWith("mode")) {
                return "STEP";
            } else {
                return null;
            }
        };

        double [] sla = new double[10];
        for (int i = 0; i < sla.length; ++i) {
            sla[i] = TimeUnit.MILLISECONDS.toNanos(100 + i * 100);
        }
        
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(registryConfig, Clock.SYSTEM);
        MeterFilter filter = new MeterFilter() {
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)
                        .percentiles(0.5, 0.75, 0.9, 0.95, 1.0)
                        .serviceLevelObjectives(sla)
                        .expiry(Duration.ofSeconds(10))
                        .bufferLength(10)
                        .build().merge(config);
            }
        };
        meterRegistry.config().meterFilter(filter);
        
        CompositeMetricsPublisher publisher = new CompositeMetricsPublisher();
        // publisher.add(new GaugePublisher(meterRegistry));
        // publisher.add(new CounterPublisher(meterRegistry));
        publisher.add(new TimerPublisher(meterRegistry));
        publisher.start();

        MetricsConsumer consumer = new MetricsConsumer(meterRegistry, 10000);
        consumer.start();
    }
}

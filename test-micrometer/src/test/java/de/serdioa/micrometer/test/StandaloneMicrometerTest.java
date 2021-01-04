package de.serdioa.micrometer.test;

import java.time.Duration;

import io.micrometer.core.instrument.Clock;
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

        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(registryConfig, Clock.SYSTEM);

        CompositeMetricsPublisher publisher = new CompositeMetricsPublisher();
        // publisher.add(new GaugePublisher(meterRegistry));
        // publisher.add(new CounterPublisher(meterRegistry));
        publisher.add(new TimerPublisher(meterRegistry));
        publisher.start();

        MetricsConsumer consumer = new MetricsConsumer(meterRegistry, 10000);
        consumer.start();
    }
}

package de.serdioa.micrometer.test;


import java.time.Duration;

import de.serdioa.micrometer.logging.direct.DirectLoggingMeterRegistry;
import de.serdioa.micrometer.logging.direct.DirectLoggingRegistryConfig;
import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;


public class Main {

    private final MetricsPublisher publisher;
    private final MetricsConsumer consumer;
    private final MeterRegistry meterRegistry;


    public static void main(String[] args) throws Exception {
        final Main main = new Main();
        main.start();

        Thread.sleep(30000);

        main.stop();
    }


    private Main() {
        final MeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

        final DirectLoggingRegistryConfig directLoggingRegistryConfig = new DirectLoggingRegistryConfig() {
            @Override
            public Duration step() {
                return Duration.ofSeconds(10);
            }


            @Override
            public String get(String key) {
                return null;
            }
        };
        final DirectLoggingMeterRegistry directLoggingMeterRegistry
                = new DirectLoggingMeterRegistry(directLoggingRegistryConfig);

        final LoggingRegistryConfig loggingRegistryConfig = new LoggingRegistryConfig() {
            @Override
            public Duration step() {
                return Duration.ofSeconds(10);
            }


            @Override
            public String get(String key) {
                return null;
            }
        };
        final LoggingMeterRegistry loggingMeterRegistry = new LoggingMeterRegistry(loggingRegistryConfig);

        final CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
        compositeRegistry.add(simpleMeterRegistry);
        compositeRegistry.add(loggingMeterRegistry);

        this.meterRegistry = compositeRegistry;

        CompositeMetricsPublisher compositePublisher = new CompositeMetricsPublisher();
        // compositePublisher.add(new CounterPublisher(meterRegistry));
        // compositePublisher.add(new DistributionSummaryPublisher(meterRegistry));
        // compositePublisher.add(new FunctionCounterPublisher(meterRegistry));
        // compositePublisher.add(new FunctionTimerPublisher(meterRegistry));
        // compositePublisher.add(new JvmMetricsPublisher(meterRegistry));
        // compositePublisher.add(new GaugePublisher(meterRegistry));
        // compositePublisher.add(new LongTaskTimerPublisher(meterRegistry));
        // compositePublisher.add(new TimeGaugePublisher(meterRegistry));
        compositePublisher.add(new TimerPublisher(meterRegistry));
        this.publisher = compositePublisher;

        this.consumer = new MetricsConsumer(simpleMeterRegistry);
    }


    private void start() {
        System.out.println("Starting workers...");

        this.publisher.start();
        this.consumer.start();

        System.out.println("Workers has been started");
    }


    private void stop() throws InterruptedException {
        System.out.println("Stopping workers...");

        this.publisher.stop();
        this.consumer.stop();

        System.out.println("Workers has been stopped");

        this.meterRegistry.close();
        System.out.println("Meter registry has been closed");
    }
}

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

    private final Thread publisherThread;

    private final MeterRegistry meterRegistry;

    public static void main(String [] args) throws Exception {
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
        final DirectLoggingMeterRegistry directLoggingMeterRegistry =
                new DirectLoggingMeterRegistry(directLoggingRegistryConfig);

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

        this.publisher = new MetricsPublisher(this.meterRegistry);
        this.consumer = new MetricsConsumer(this.meterRegistry);

        this.publisherThread = new Thread(this.publisher);
    }


    private void start() {
        System.out.println("Starting threads...");

        this.publisherThread.start();
        this.consumer.start();

        System.out.println("Threads has been started");
    }


    private void stop() throws InterruptedException {
        System.out.println("Stopping threads...");

        this.publisherThread.interrupt();
        this.publisherThread.join();

        this.consumer.stop();

        System.out.println("Threads has been stopped");

        this.meterRegistry.close();
        System.out.println("Meter registry has been closed");
    }
}

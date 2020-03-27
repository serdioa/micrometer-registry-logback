package de.serdioa.micrometer.test;

import java.util.Objects;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;


public class JvmMetricsPublisher implements Runnable {

    private final MeterRegistry meterRegistry;


    public JvmMetricsPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("JvmMetricsPublisher has been started");

        JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();
        JvmThreadMetrics jvmThreadMetrics = new JvmThreadMetrics();
        try (JvmGcMetrics jvmGcMetrics = new JvmGcMetrics()) {

            jvmMemoryMetrics.bindTo(this.meterRegistry);
            jvmGcMetrics.bindTo(this.meterRegistry);
            jvmThreadMetrics.bindTo(this.meterRegistry);

            while (true) {
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (InterruptedException ex) {
            System.out.println("JvmMetricsPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

package de.serdioa.micrometer.test;

import java.util.Objects;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;


public class JvmMetricsPublisher implements MetricsPublisher {

    private MeterRegistry registry;

    private JvmMemoryMetrics jvmMemoryMetrics;
    private JvmThreadMetrics jvmThreadMetrics;
    private JvmGcMetrics jvmGcMetrics;


    public JvmMetricsPublisher(MeterRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }


    @Override
    public void start() {
        System.out.println("Starting " + this.getClass().getSimpleName());

        this.jvmMemoryMetrics = new JvmMemoryMetrics();
        this.jvmThreadMetrics = new JvmThreadMetrics();
        this.jvmGcMetrics = new JvmGcMetrics();

        jvmMemoryMetrics.bindTo(this.registry);
        jvmGcMetrics.bindTo(this.registry);
        jvmThreadMetrics.bindTo(this.registry);

        System.out.println("Started " + this.getClass().getSimpleName());
    }


    @Override
    public void stop() throws InterruptedException {
        System.out.println("Stopping " + this.getClass().getSimpleName());

        this.jvmGcMetrics.close();

        System.out.println("Stopped " + this.getClass().getSimpleName());
    }
}

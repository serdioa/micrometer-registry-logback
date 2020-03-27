package de.serdioa.micrometer.test;

import java.util.Objects;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;


public class MetricsConsumer implements Runnable {
    private final MeterRegistry meterRegistry;
    private final boolean enabled;

    public MetricsConsumer(MeterRegistry meterRegistry) {
        this(meterRegistry, true);
    }


    public MetricsConsumer(MeterRegistry meterRegistry, boolean enabled) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.enabled = enabled;
    }


    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                consume();
            }
        } catch (InterruptedException ex) {
            System.out.println("Consumer has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void consume() throws InterruptedException {
        if (enabled) {
            final StringBuilder sb = new StringBuilder("Consumer:\n");

            for (Meter m : this.meterRegistry.getMeters()) {
                sb.append("    ").append(publishMeter(m)).append("\n");
            }

            System.out.println(sb);
        }

        Thread.sleep(1000);
    }


    private String publishMeter(Meter meter) {
        StringBuilder sb = new StringBuilder(meter.getId().toString());
        sb.append(": ");

        boolean first = true;
        for (Measurement m : meter.measure()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(m.getStatistic()).append("=").append(m.getValue());
        }

        return sb.toString();
    }
}

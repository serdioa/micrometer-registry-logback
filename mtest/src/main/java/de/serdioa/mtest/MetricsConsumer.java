package de.serdioa.mtest;

import java.util.Objects;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;


public class MetricsConsumer implements Runnable {
    private final MeterRegistry meterRegistry;
    private final Timer timer;

    public MetricsConsumer(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
        this.timer = this.meterRegistry.timer("publisher.timer");
    }


    @Override
    public void run() {
        try {
            while (true) {
                consume();
            }
        } catch (InterruptedException ex) {
            System.out.println("Consumer has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void consume() throws InterruptedException {
        final StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        for (Measurement m : this.timer.measure()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(m.getStatistic()).append("=").append(m.getValue());
        }
        System.out.println("Consumer: " + sb);
        
        Thread.sleep(1000);
    }
}

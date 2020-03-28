package de.serdioa.micrometer.spring.test;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MetricsConsumer {
    @Autowired
    private MeterRegistry registry;

    @Scheduled(fixedRate = 5000)
    public void consumeMetrics() {
        String metrics = buildMetrics();
        System.out.println(metrics);
    }


    private String buildMetrics() {
        StringBuilder sb = new StringBuilder("Metrics:\n");

        for (Meter meter : this.registry.getMeters()) {
            sb.append(buildMetrics(meter));
        }

        return sb.toString();
    }


    private String buildMetrics(Meter meter) {
        StringBuilder sb = new StringBuilder("    ");
        sb.append(meter.getId().toString());
        sb.append(":\n");

        for (Measurement measurement : meter.measure()) {
            sb.append("        ");
            sb.append(measurement.getStatistic().getTagValueRepresentation());
            sb.append("=");
            sb.append(measurement.getValue());
            sb.append("\n");
        }

        return sb.toString();
    }
}

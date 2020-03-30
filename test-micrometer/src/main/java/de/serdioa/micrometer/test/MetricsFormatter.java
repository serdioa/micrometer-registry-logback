package de.serdioa.micrometer.test;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.StringEscapeUtils;


public class MetricsFormatter {

    public static void format(MeterRegistry registry, StringBuilder sb) {
        sb.append("Metrics:\n");

        for (Meter meter : registry.getMeters()) {
            sb.append(format(meter));
        }
    }


    public static String format(MeterRegistry registry) {
        StringBuilder sb = new StringBuilder();

        format(registry, sb);

        return sb.toString();
    }


    public static void format(Meter meter, StringBuilder sb) {
        final String escapedMeterId = StringEscapeUtils.escapeJson(meter.getId().toString());

        sb.append("    ")
                .append(escapedMeterId)
                .append(":\n");

        for (Measurement measurement : meter.measure()) {
            sb.append("        ")
                    .append(format(measurement))
                    .append("\n");
        }
    }


    public static String format(Meter meter) {
        StringBuilder sb = new StringBuilder();
        format(meter, sb);
        return sb.toString();
    }


    public static void format(Measurement measurement, StringBuilder sb) {
        sb.append(measurement.getStatistic().getTagValueRepresentation())
                .append('=')
                .append(measurement.getValue());
    }


    public static String format(Measurement measurement) {
        StringBuilder sb = new StringBuilder();
        format(measurement, sb);
        return sb.toString();
    }
}

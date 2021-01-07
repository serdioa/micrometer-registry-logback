package de.serdioa.micrometer.test;



import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
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

        if (meter instanceof Timer) {
            formatTimer((Timer) meter, sb);
        } else {
            for (Measurement measurement : meter.measure()) {
                sb.append("        ")
                        .append(format(measurement))
                        .append("\n");
            }
        }
    }


    public static void formatTimer(Timer timer, StringBuilder sb) {
        HistogramSnapshot snapshot = timer.takeSnapshot();
        
        sb.append("        ").append("count=").append(snapshot.count()).append("\n");
        sb.append("        ").append("total=").append(snapshot.total(timer.baseTimeUnit())).append("\n");
        sb.append("        ").append("mean=").append(snapshot.mean(timer.baseTimeUnit())).append("\n");
        sb.append("        ").append("max=").append(snapshot.max(timer.baseTimeUnit())).append("\n");
        
        CountAtBucket [] counts = snapshot.histogramCounts();
        if (counts.length > 0) {
            sb.append("        ").append("counts=(");
            for (int i = 0; i < counts.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                
                sb.append(counts[i].bucket(timer.baseTimeUnit()));
                sb.append(":");
                sb.append(counts[i].count());
            }
            sb.append(")\n");
        }
        
        ValueAtPercentile [] percentiles = snapshot.percentileValues();
        if (percentiles.length > 0) {
            sb.append("        ").append("percentiles=(");
            for (int i = 0; i < percentiles.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                
                sb.append(percentiles[i].percentile());
                sb.append(":");
                sb.append(percentiles[i].value(timer.baseTimeUnit()));
            }
            sb.append(")\n");
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

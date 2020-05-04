package de.serdioa.micrometer.logging.agg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import net.logstash.logback.argument.StructuredArgument;


public class JsonLongTaskTimerSnapshot extends LongTaskTimerSnapshot implements StructuredArgument {

    public JsonLongTaskTimerSnapshot(LongTaskTimer longTaskTimer) {
        super(longTaskTimer);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "ltt");
        generator.writeStringField("unit", this.baseTimeUnit.toString());
        generator.writeNumberField("tasks", this.activeTasks);

        generator.writeNumberField("cnt", this.histogramSnapshot.count());
        generator.writeNumberField("mean", this.histogramSnapshot.mean(this.baseTimeUnit));
        generator.writeNumberField("max", this.histogramSnapshot.max(this.baseTimeUnit));
        generator.writeNumberField("total", this.histogramSnapshot.total(this.baseTimeUnit));

        // Histogram

        CountAtBucket[] buckets = this.histogramSnapshot.histogramCounts();
        final int bucketsLength = buckets.length;
        if (bucketsLength > 0) {
            // Histogram buckets
            generator.writeArrayFieldStart("hb");
            for (int i = 0; i < bucketsLength; ++i) {
                generator.writeNumber(buckets[i].bucket(this.baseTimeUnit));
            }
            generator.writeEndArray();

            // Histogram count in bucket
            generator.writeArrayFieldStart("hc");
            for (int i = 0; i < bucketsLength; ++i) {
                generator.writeNumber((long) buckets[i].count());
            }
            generator.writeEndArray();
        }

        // Percentiles

        ValueAtPercentile[] percentiles = this.histogramSnapshot.percentileValues();
        final int percentilesLength = percentiles.length;
        if (percentilesLength > 0) {
            // Percentile buckets
            generator.writeArrayFieldStart("pb");
            for (int i = 0; i < percentilesLength; ++i) {
                generator.writeNumber(percentiles[i].percentile());
            }
            generator.writeEndArray();

            // Values at percentiles
            generator.writeArrayFieldStart("pv");
            for (int i = 0; i < percentilesLength; ++i) {
                generator.writeNumber(percentiles[i].value(this.baseTimeUnit));
            }
            generator.writeEndArray();
        }
    }
}

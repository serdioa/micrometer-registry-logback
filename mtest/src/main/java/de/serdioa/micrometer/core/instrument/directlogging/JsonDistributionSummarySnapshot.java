package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonDistributionSummarySnapshot extends DistributionSummarySnapshot implements StructuredArgument {

    public JsonDistributionSummarySnapshot(DistributionSummary distributionSummary) {
        super(distributionSummary);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "timer");
        generator.writeNumberField("cnt", this.histogramSnapshot.count());
        generator.writeNumberField("mean", this.histogramSnapshot.mean());
        generator.writeNumberField("max", this.histogramSnapshot.max());

        CountAtBucket [] buckets = this.histogramSnapshot.histogramCounts();
        final int bucketsLength = buckets.length;

        // Histogram buckets
        generator.writeArrayFieldStart("hb");
        for (int i = 0; i < bucketsLength; ++i) {
            generator.writeNumber(buckets[i].bucket());
        }
        generator.writeEndArray();

        // Histogram count in bucket
        generator.writeArrayFieldStart("hc");
        for (int i = 0; i < bucketsLength; ++i) {
            generator.writeNumber((long) buckets[i].count());
        }
        generator.writeEndArray();
    }
}

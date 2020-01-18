package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonTimerSnapshot extends TimerSnapshot implements StructuredArgument {

    public JsonTimerSnapshot(Timer timer) {
        super(timer);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "timer");
        generator.writeStringField("unit", this.baseTimeUnit.toString());
        generator.writeNumberField("cnt", this.histogramSnapshot.count());
        generator.writeNumberField("mean", this.histogramSnapshot.mean(this.baseTimeUnit));
        generator.writeNumberField("max", this.histogramSnapshot.max(this.baseTimeUnit));
        generator.writeNumberField("total", this.histogramSnapshot.total(this.baseTimeUnit));

        CountAtBucket [] buckets = this.histogramSnapshot.histogramCounts();
        final int bucketsLength = buckets.length;

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
}

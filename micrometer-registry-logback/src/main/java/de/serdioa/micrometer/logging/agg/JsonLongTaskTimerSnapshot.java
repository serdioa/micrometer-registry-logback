package de.serdioa.micrometer.logging.agg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.LongTaskTimer;
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
        generator.writeNumberField("dur", this.duration);
    }
}

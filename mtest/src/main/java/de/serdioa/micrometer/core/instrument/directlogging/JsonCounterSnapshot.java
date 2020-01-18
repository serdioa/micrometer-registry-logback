package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Counter;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonCounterSnapshot extends CounterSnapshot implements StructuredArgument {

    public JsonCounterSnapshot(Counter counter) {
        super(counter);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "cnt");
        generator.writeNumberField("cnt", this.count);
    }
}

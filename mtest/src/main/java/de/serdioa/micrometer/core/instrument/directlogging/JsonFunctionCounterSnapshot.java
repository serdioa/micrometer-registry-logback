package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.FunctionCounter;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonFunctionCounterSnapshot extends FunctionCounterSnapshot implements StructuredArgument {

    public JsonFunctionCounterSnapshot(FunctionCounter counter) {
        super(counter);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "fcnt");
        generator.writeNumberField("cnt", this.count);
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.FunctionTimer;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonFunctionTimerSnapshot extends FunctionTimerSnapshot implements StructuredArgument {

    public JsonFunctionTimerSnapshot(FunctionTimer timer) {
        super(timer);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "ftimer");
        generator.writeStringField("unit", this.baseTimeUnit.toString());
        generator.writeNumberField("cnt", this.count);
        generator.writeNumberField("mean", (this.count == 0.0 ? 0 : this.totalTime / this.count));
        generator.writeNumberField("total", this.totalTime);
    }
}

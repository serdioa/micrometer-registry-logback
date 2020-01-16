package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Timer;
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
    }
}

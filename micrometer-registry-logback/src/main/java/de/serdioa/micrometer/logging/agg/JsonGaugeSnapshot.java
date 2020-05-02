package de.serdioa.micrometer.logging.agg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Gauge;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ class JsonGaugeSnapshot extends GaugeSnapshot implements StructuredArgument {

    public JsonGaugeSnapshot(Gauge gauge) {
        super(gauge);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "g");
        generator.writeNumberField("val", this.value);
    }
}

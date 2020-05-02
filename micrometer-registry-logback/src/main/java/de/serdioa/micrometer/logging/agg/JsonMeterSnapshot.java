package de.serdioa.micrometer.logging.agg;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.Meter;
import net.logstash.logback.argument.StructuredArgument;


/* package private */ public class JsonMeterSnapshot extends MeterSnapshot implements StructuredArgument {

    public JsonMeterSnapshot(Meter meter) {
        super(meter);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        final int size = this.names.size();
        assert (size == this.values.size());

        generator.writeStringField("t", this.id.getType().toString());
        generator.writeStringField("unit", this.id.getBaseUnit());

        for (int i = 0; i < size; ++i) {
            generator.writeNumberField(this.names.get(i), this.values.get(i));
        }
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.TimeGauge;
import net.logstash.logback.argument.StructuredArgument;


public class JsonTimeGaugeSnapshot extends TimeGaugeSnapshot implements StructuredArgument {

    public JsonTimeGaugeSnapshot(TimeGauge timeGauge) {
        super(timeGauge);
    }


    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeStringField("t", "tg");
        generator.writeStringField("unit", this.baseTimeUnit.toString());
        generator.writeNumberField("val", this.value);
    }
}

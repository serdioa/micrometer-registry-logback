package de.serdioa.micrometer.logging.direct;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import de.serdioa.micrometer.logging.base.LoggingCounter;
import io.micrometer.core.instrument.Clock;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class DirectLoggingCounter extends LoggingCounter {

    public DirectLoggingCounter(Id id, Clock clock, long stepMillis, Logger logger, Marker tags) {
        super(id, clock, stepMillis, logger, tags);
    }


    @Override
    public void increment(double amount) {
        super.increment(amount);

        final Logger logger = this.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(this.getTags(), null, new Event(amount));
        }
    }


    private static class Event implements StructuredArgument {

        private final double amount;


        public Event(double amount) {
            this.amount = amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("t", "cnt");
            generator.writeNumberField("amt", this.amount);
        }
    }
}

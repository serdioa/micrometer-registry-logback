package de.serdioa.micrometer.logging.direct;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import de.serdioa.micrometer.logging.base.LoggingDistributionSummary;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class DirectLoggingDistributionSummary extends LoggingDistributionSummary {

    public DirectLoggingDistributionSummary(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis, boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
        super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles, logger, tags);
    }


    @Override
    protected void recordNonNegative(double amount) {
        super.recordNonNegative(amount);

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
            generator.writeStringField("t", "ds");
            generator.writeNumberField("amt", this.amount);
        }
    }
}

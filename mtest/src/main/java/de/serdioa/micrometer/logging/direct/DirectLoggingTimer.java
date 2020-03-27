package de.serdioa.micrometer.logging.direct;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import de.serdioa.micrometer.logging.base.LoggingTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class DirectLoggingTimer extends LoggingTimer {

    public DirectLoggingTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis, boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
        super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis, supportsAggregablePercentiles, logger, tags);
    }


    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        super.recordNonNegative(amount, unit);

        final Logger logger = this.getLogger();
        if (logger.isInfoEnabled()) {
            long nanoseconds = unit.toNanos(amount);

            logger.info(this.getTags(), null, new Event(nanoseconds));
        }
    }


    private static class Event implements StructuredArgument {

        private final long amount;


        public Event(long amount) {
            this.amount = amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("t", "timer");
            generator.writeNumberField("amt", this.amount);
        }
    }
}

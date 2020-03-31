package de.serdioa.micrometer.logging.direct;

import java.util.Objects;

import de.serdioa.micrometer.logging.base.AbstractLoggingMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class DirectLoggingMeterRegistry extends AbstractLoggingMeterRegistry {

    private final DirectLoggingRegistryConfig config;


    public DirectLoggingMeterRegistry() {
        this(DirectLoggingRegistryConfig.DEFAULT);
    }


    public DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config) {
        this(config, Clock.SYSTEM);
    }


    public DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config, Clock clock) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);
    }


    @Override
    protected void publish() {
        // No-op: this meter registry implementation publishes does not publish metrics on schedule.
        // Instead, metrics are published directly when they are updated.
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        Logger logger = this.getMeterLogger(id);
        Marker tags = this.getTags(id);

        return new DirectLoggingTimer(id, this.clock, distributionStatisticConfig, pauseDetector, this.getBaseTimeUnit(),
                this.config.step().toMillis(), false, logger, tags);
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        Logger logger = this.getMeterLogger(id);
        Marker tags = this.getTags(id);

        return new DirectLoggingCounter(id, this.clock, this.config.step().toMillis(), logger, tags);
    }


    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id,
            DistributionStatisticConfig distributionStatisticConfig, double scale) {
        Logger logger = this.getMeterLogger(id);
        Marker tags = this.getTags(id);

        return new DirectLoggingDistributionSummary(id, this.clock, distributionStatisticConfig, scale,
                this.config.step().toMillis(), false, logger, tags);
    }


    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        Logger logger = this.getMeterLogger(id);
        Marker tags = this.getTags(id);

        return new DirectLoggingLongTaskTimer(id, this.clock, logger, tags);
    }
}

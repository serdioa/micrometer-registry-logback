package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.NamedThreadFactory;


public class DirectLoggingMeterRegistry extends StepMeterRegistry {

    private final DirectLoggingRegistryConfig config;

    private final DirectMeterLogger logger;

    public DirectLoggingMeterRegistry() {
        this(DirectLoggingRegistryConfig.DEFAULT);
    }


    public DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config) {
        this(config, Clock.SYSTEM, new NamedThreadFactory("direct-logging-metrics-publisher"));
    }


    private DirectLoggingMeterRegistry(DirectLoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        this.config = Objects.requireNonNull(config);
        this.logger = new Slf4jLogstashJsonMeterLogger();
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    @Override
    protected void publish() {
        System.out.println("DirectLoggingMeterRegistry::publish()");
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new DirectLoggingTimer(id, clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                this.config.step().toMillis(), false, this.logger);
    }
}

package de.serdioa.micrometer.pull;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;


public abstract class PullMeterRegistry extends MeterRegistry {

    protected final PullConfig pullConfig;


    protected PullMeterRegistry(PullConfig config, Clock clock) {
        super(clock);

        config.requireValid();
        this.pullConfig = config;
    }


    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }


    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder()
                .expiry(this.pullConfig.pollingFrequency())
                .build()
                .merge(DistributionStatisticConfig.DEFAULT);
    }
}

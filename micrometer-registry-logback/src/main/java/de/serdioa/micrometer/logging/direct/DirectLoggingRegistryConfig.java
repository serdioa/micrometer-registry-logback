package de.serdioa.micrometer.logging.direct;

import io.micrometer.core.instrument.step.StepRegistryConfig;


public interface DirectLoggingRegistryConfig extends StepRegistryConfig {

    DirectLoggingRegistryConfig DEFAULT = k -> null;


    @Override
    default String prefix() {
        return "metrics.direct";
    }
}

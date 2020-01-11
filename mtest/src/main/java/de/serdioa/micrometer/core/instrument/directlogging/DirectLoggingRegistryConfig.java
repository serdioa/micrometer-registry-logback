package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.step.StepRegistryConfig;


public interface DirectLoggingRegistryConfig extends StepRegistryConfig {
    DirectLoggingRegistryConfig DEFAULT = k -> null;


    @Override
    default String prefix() {
        return "metrics.direct";
    }
}

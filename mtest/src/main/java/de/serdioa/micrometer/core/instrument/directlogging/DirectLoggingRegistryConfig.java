package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.step.StepRegistryConfig;


public interface DirectLoggingRegistryConfig extends StepRegistryConfig {
    DirectLoggingRegistryConfig DEFAULT = k -> null;

    @Override
    default String prefix() {
        return "directLogging";
    }


    /**
     * @return Whether counters and timers that have no activity in an
     * interval are still logged.
     */
    default boolean logInactive() {
        String v = get(prefix() + ".logInactive");
        return Boolean.parseBoolean(v);
    }
}

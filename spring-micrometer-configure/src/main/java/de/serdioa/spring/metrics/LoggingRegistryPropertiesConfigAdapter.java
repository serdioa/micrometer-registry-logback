package de.serdioa.spring.metrics;

import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;


/**
 * Adapter which presents configuration properties as Micrometer {@link LoggingRegistryConfig}.
 */
public class LoggingRegistryPropertiesConfigAdapter
        extends StepRegistryPropertiesConfigAdapter<LoggingRegistryProperties> implements LoggingRegistryConfig {

    public LoggingRegistryPropertiesConfigAdapter(LoggingRegistryProperties properties) {
        super(properties);
    }


    @Override
    public String prefix() {
        return get(LoggingRegistryProperties::getPrefix, LoggingRegistryConfig.super::prefix);
    }


    @Override
    public boolean logInactive() {
        return this.get(LoggingRegistryProperties::isLogInactive, LoggingRegistryConfig.super::logInactive);
    }
}

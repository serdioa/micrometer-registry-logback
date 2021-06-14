package de.serdioa.spring.configure.metrics.export.logging.agg;

import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapter;


public class LoggingPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<LoggingProperties>
        implements LoggingRegistryConfig {

    public LoggingPropertiesConfigAdapter(LoggingProperties properties) {
        super(properties);
    }


    @Override
    public String prefix() {
        return get(LoggingProperties::getPrefix, LoggingRegistryConfig.super::prefix);
    }


    @Override
    public boolean logInactive() {
        return get(LoggingProperties::isLogInactive, () -> true);
    }
}

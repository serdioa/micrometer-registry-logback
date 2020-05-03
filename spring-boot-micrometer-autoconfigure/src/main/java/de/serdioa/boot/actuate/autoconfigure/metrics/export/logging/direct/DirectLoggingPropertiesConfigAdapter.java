package de.serdioa.boot.actuate.autoconfigure.metrics.export.logging.direct;

import de.serdioa.micrometer.logging.direct.DirectLoggingRegistryConfig;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapter;


public class DirectLoggingPropertiesConfigAdapter extends StepRegistryPropertiesConfigAdapter<DirectLoggingProperties>
        implements DirectLoggingRegistryConfig {

    public DirectLoggingPropertiesConfigAdapter(DirectLoggingProperties properties) {
        super(properties);
    }


    @Override
    public String prefix() {
        return get(DirectLoggingProperties::getPrefix, DirectLoggingRegistryConfig.super::prefix);
    }
}

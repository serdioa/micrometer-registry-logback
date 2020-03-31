package de.serdioa.boot.actuate.autoconfigure.metrics.export.logging.direct;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "management.metrics.export.logging.direct")
public class DirectLoggingProperties extends StepRegistryProperties {

    @Getter
    @Setter
    private String prefix = "metrics.direct";
}

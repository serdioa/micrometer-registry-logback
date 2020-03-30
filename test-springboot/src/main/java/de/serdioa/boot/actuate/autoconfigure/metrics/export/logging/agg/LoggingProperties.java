package de.serdioa.boot.actuate.autoconfigure.metrics.export.logging.agg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "management.metrics.export.logging.agg")
public class LoggingProperties extends StepRegistryProperties {

    @Getter
    @Setter
    private String prefix = "metrics";

    @Getter
    @Setter
    private boolean logInactive = true;
}

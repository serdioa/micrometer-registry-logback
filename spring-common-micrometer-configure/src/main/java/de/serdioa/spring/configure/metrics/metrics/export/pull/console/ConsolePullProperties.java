package de.serdioa.spring.configure.metrics.metrics.export.pull.console;

import java.time.Duration;

import de.serdioa.spring.configure.metrics.filter.FilterProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "management.metrics.export.pull.console")
public class ConsolePullProperties extends StepRegistryProperties {

    @Getter
    @Setter
    private boolean enabled = true;

    @Getter
    @Setter
    private Duration pollingFrequency = Duration.ofMinutes(1);

    @Getter
    private final FilterProperties filter = new FilterProperties();
}

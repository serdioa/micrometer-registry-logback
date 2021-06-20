package de.serdioa.spring.metrics;

import java.time.Duration;

import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingProperties;
import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingPropertiesConfigAdapter;
import de.serdioa.spring.configure.metrics.filter.FilterMeterRegistryCustomizer;
import de.serdioa.spring.configure.properties.HierarchicalProperties;
import de.serdioa.spring.configure.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LoggingRegistryConfiguration {

    private static final String PROP_PREFIX = "management.metrics.export.logging.agg";
    private static final String PROP_ENABLED = PROP_PREFIX + ".enabled";
    private static final String PROP_STEP = PROP_PREFIX + ".step";
    private static final String PROP_FILTER_ENABLED = PROP_PREFIX + ".filter.enabled";

    private static final Duration DEFAULT_STEP = Duration.ofSeconds(10);


    @Bean
    public LoggingProperties loggingRegistryProperties(StructuredPropertyService propertyService) {
        LoggingProperties props = new LoggingProperties();

        Boolean enabled = propertyService.getProperty(PROP_ENABLED, Boolean.class, true);
        props.setEnabled(enabled);

        String stepStr = propertyService.getProperty(PROP_STEP);
        if (stepStr != null) {
            Duration step = DurationStyle.detectAndParse(stepStr);
            props.setStep(step);
        } else {
            props.setStep(DEFAULT_STEP);
        }

        HierarchicalProperties<Boolean> filterEnabled = propertyService.getProperties(
                PROP_FILTER_ENABLED, Boolean::valueOf);
        props.getFilter().getEnabled().putAll(filterEnabled);

        return props;
    }


    @Bean
    public LoggingRegistryConfig loggingRegistryConfig(LoggingProperties props) {
        return new LoggingPropertiesConfigAdapter(props);
    }


    @Bean
    public MeterRegistry loggingMeterRegistry(LoggingRegistryConfig config, Clock clock) {
        return new LoggingMeterRegistry(config, clock);
    }


    @Bean
    public MeterRegistryCustomizer<LoggingMeterRegistry> loggingFilterMeterRegistryCustomizer(LoggingProperties props) {
        return new FilterMeterRegistryCustomizer<>(LoggingMeterRegistry.class, props.getFilter());
    }
}

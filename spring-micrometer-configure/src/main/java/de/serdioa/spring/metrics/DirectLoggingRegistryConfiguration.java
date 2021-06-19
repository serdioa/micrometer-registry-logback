package de.serdioa.spring.metrics;

import de.serdioa.micrometer.logging.direct.DirectLoggingMeterRegistry;
import de.serdioa.micrometer.logging.direct.DirectLoggingRegistryConfig;
import de.serdioa.spring.configure.metrics.export.logging.direct.DirectLoggingProperties;
import de.serdioa.spring.configure.metrics.export.logging.direct.DirectLoggingPropertiesConfigAdapter;
import de.serdioa.spring.configure.metrics.filter.FilterMeterRegistryCustomizer;
import de.serdioa.spring.configure.properties.HierarchicalProperties;
import de.serdioa.spring.configure.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DirectLoggingRegistryConfiguration {

    private static final String PROP_PREFIX = "management.metrics.export.logging.direct";
    private static final String PROP_ENABLED = PROP_PREFIX + ".enabled";
    private static final String PROP_FILTER_ENABLED = PROP_PREFIX + ".filter.enabled";


    @Bean
    public DirectLoggingProperties directLoggingRegistryProperties(StructuredPropertyService propertyService) {
        DirectLoggingProperties props = new DirectLoggingProperties();

        Boolean enabled = propertyService.getProperty(PROP_ENABLED, Boolean.class, true);
        props.setEnabled(enabled);

        HierarchicalProperties<Boolean> filterEnabled = propertyService.getProperties(
                PROP_FILTER_ENABLED, Boolean::valueOf);
        props.getFilter().getEnabled().putAll(filterEnabled);

        return props;
    }


    @Bean
    public DirectLoggingRegistryConfig directLoggingRegistryConfig(DirectLoggingProperties props) {
        return new DirectLoggingPropertiesConfigAdapter(props);
    }


    @Bean
    public MeterRegistry directLoggingMeterRegistry(DirectLoggingRegistryConfig config, Clock clock) {
        return new DirectLoggingMeterRegistry(config, clock);
    }


    @Bean
    public MeterRegistryCustomizer<DirectLoggingMeterRegistry> directLoggingFilterMeterRegistryCustomizer(DirectLoggingProperties props) {
        return new FilterMeterRegistryCustomizer<>(DirectLoggingMeterRegistry.class, props.getFilter());
    }
}

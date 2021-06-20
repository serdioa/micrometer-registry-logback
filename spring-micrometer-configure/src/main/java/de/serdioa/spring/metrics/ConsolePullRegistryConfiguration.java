package de.serdioa.spring.metrics;

import java.time.Duration;
import java.util.Map;

import de.serdioa.micrometer.pull.console.ConsolePullConfig;
import de.serdioa.micrometer.pull.console.ConsolePullMeterRegistry;
import de.serdioa.spring.configure.metrics.filter.FilterMeterRegistryCustomizer;
import de.serdioa.spring.configure.metrics.metrics.export.pull.console.ConsolePullProperties;
import de.serdioa.spring.configure.metrics.metrics.export.pull.console.ConsolePullPropertiesConfigAdapter;
import de.serdioa.spring.configure.properties.HierarchicalProperties;
import de.serdioa.spring.configure.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConsolePullRegistryConfiguration {

    private static final String PROP_PREFIX = "management.metrics.export.pull.console";
    private static final String PROP_ENABLED = PROP_PREFIX + ".enabled";
    private static final String PROP_POLLING_FREQUENCY = PROP_PREFIX + ".pollingFrequency";
    private static final String PROP_FILTER_ENABLED = PROP_PREFIX + ".filter.enabled";
    private static final String PROP_RENAME = PROP_PREFIX + ".filter.rename";


    @Bean
    public ConsolePullProperties consolePullRegistryProperties(StructuredPropertyService propertyService) {
        ConsolePullProperties props = new ConsolePullProperties();

        Boolean enabled = propertyService.getProperty(PROP_ENABLED, Boolean.class, true);
        props.setEnabled(enabled);

        String pollingFrequencyStr = propertyService.getProperty(PROP_POLLING_FREQUENCY);
        if (pollingFrequencyStr != null) {
            Duration pollingFrequency = DurationStyle.detectAndParse(pollingFrequencyStr);
            props.setPollingFrequency(pollingFrequency);
        }

        HierarchicalProperties<Boolean> filterEnabled = propertyService.getProperties(
                PROP_FILTER_ENABLED, Boolean::valueOf);
        props.getFilter().getEnabled().putAll(filterEnabled);

        Map<String, String> rename = propertyService.getProperties(PROP_RENAME);
        props.getFilter().getRename().putAll(rename);

        return props;
    }


    @Bean
    public ConsolePullConfig consolePullRegistryConfig(ConsolePullProperties props) {
        return new ConsolePullPropertiesConfigAdapter(props);
    }


    @Bean
    public MeterRegistry consolePullMeterRegistry(ConsolePullConfig config, Clock clock) {
        return new ConsolePullMeterRegistry(config, clock);
    }


    @Bean
    public MeterRegistryCustomizer<ConsolePullMeterRegistry> consolePullFilterMeterRegistryCustomizer(ConsolePullProperties props) {
        return new FilterMeterRegistryCustomizer<>(ConsolePullMeterRegistry.class, props.getFilter());
    }
}

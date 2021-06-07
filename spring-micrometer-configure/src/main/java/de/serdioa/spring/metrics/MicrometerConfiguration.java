package de.serdioa.spring.metrics;

import java.util.List;
import java.util.Map;

import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class MicrometerConfiguration {

    @Bean
    public StructuredPropertyService propertiesService() {
        return new StructuredPropertyService();
    }


    @Bean
    public Clock micrometerClock() {
        return Clock.SYSTEM;
    }


    @Bean
    @Primary
    public MeterRegistry compositeMeterRegistry(Clock clock, List<MeterRegistry> registries) {
        return new CompositeMeterRegistry(clock, registries);
    }


    @Bean
    public MetricsProperties micrometerMetricsProperties(StructuredPropertyService structuredPropertyService) {
        Map<String, Boolean> enable = structuredPropertyService
                .getProperties("management.metrics.enable", Boolean::valueOf);
        Map<String, String> tags = structuredPropertyService.getProperties("management.metrics.tags");

        return new MetricsProperties(enable, tags);
    }


    @Bean
    public MetricsPropertiesFilter micrometerMetricsPropertiesFilter(MetricsProperties metricsProperties) {
        return new MetricsPropertiesFilter(metricsProperties);
    }


    @Bean
    public MeterRegistryPostProcessor micrometerMetricsRegistryPostProcessor(ObjectProvider<MeterFilter> meterFilters) {
        return new MeterRegistryPostProcessor(meterFilters);
    }
}

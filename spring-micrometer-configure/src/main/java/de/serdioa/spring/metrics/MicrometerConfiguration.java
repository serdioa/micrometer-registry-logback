package de.serdioa.spring.metrics;

import java.util.List;
import java.util.Map;

import de.serdioa.spring.configure.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
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
        Map<String, Boolean> percentilesHistogram = structuredPropertyService.getProperties(
                "management.metrics.distribution.percentiles-histogram", Boolean::valueOf);
        Map<String, double[]> percentiles = structuredPropertyService.getProperties(
                "management.metrics.distribution.percentiles", this::parseDoubleArray);
        Map<String, MeterValue> minimumExpectedValue = structuredPropertyService.getProperties(
                "management.metrics.distribution.minimum-expected-value", MeterValue::of);
        Map<String, MeterValue> maximumExpectedValue = structuredPropertyService.getProperties(
                "management.metrics.distribution.maximum-expected-value", MeterValue::of);
        Map<String, MeterValue[]> sla = structuredPropertyService.getProperties(
                "management.metrics.distribution.sla", this::parseMeterValueArray);

        return new MetricsProperties(enable, tags, percentilesHistogram, percentiles,
                minimumExpectedValue, maximumExpectedValue, sla);
    }


    private double[] parseDoubleArray(String str) {
        // Split by comma.
        String[] items = str.split(",");

        // Parse each item as a Double.
        double[] result = new double[items.length];
        for (int i = 0; i < items.length; ++i) {
            result[i] = Double.parseDouble(items[i]);
        }

        return result;
    }


    private MeterValue[] parseMeterValueArray(String str) {
        // Split by comma.
        String[] items = str.split(",");

        // Parse each item as a MeterValue (i.e. either an integer/long or a Duration).
        MeterValue[] result = new MeterValue[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = MeterValue.of(items[i]);
        }

        return result;
    }


    @Bean
    public MetricsPropertiesFilter micrometerMetricsPropertiesFilter(MetricsProperties metricsProperties) {
        return new MetricsPropertiesFilter(metricsProperties);
    }


    @Bean
    public MeterRegistryPostProcessor micrometerMetricsRegistryPostProcessor(
            ObjectProvider<MeterRegistryCustomizer> customizers, ObjectProvider<MeterFilter> filters,
            ObjectProvider<MeterBinder> binders) {
        return new MeterRegistryPostProcessor(customizers, filters, binders);
    }
}

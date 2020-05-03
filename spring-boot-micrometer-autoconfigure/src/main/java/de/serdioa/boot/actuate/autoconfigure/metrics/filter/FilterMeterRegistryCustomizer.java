package de.serdioa.boot.actuate.autoconfigure.metrics.filter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;


public class FilterMeterRegistryCustomizer<T extends MeterRegistry> implements MeterRegistryCustomizer<T> {

    private final FilterProperties filterProperties;


    public FilterMeterRegistryCustomizer(FilterProperties filterProperties) {
        this.filterProperties = Objects.requireNonNull(filterProperties);
    }


    @Override
    public void customize(T registry) {
        this.addEnabledFilter(registry);
    }


    // Add to the specified registry a filter which enables / disables meters based on the provided configuration
    // properties.
    private void addEnabledFilter(T registry) {
        Map<String, Boolean> enabled = this.filterProperties.getEnabled();

        if (enabled.isEmpty()) {
            // Do not create a no-op filter if no rules are specified (expected common case).
            return;
        }

        Predicate<Meter.Id> denyPredicate = new PropertiesMeterIdDenyPredicate(enabled);
        MeterFilter enabledFilter = MeterFilter.deny(denyPredicate);
        registry.config().meterFilter(enabledFilter);
    }
}

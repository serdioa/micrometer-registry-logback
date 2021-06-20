package de.serdioa.spring.configure.metrics.filter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;


public class FilterMeterRegistryCustomizer<T extends MeterRegistry> extends TypedMeterRegistryCustomizer<T> {

    private final FilterProperties filterProperties;


    public FilterMeterRegistryCustomizer(Class<T> type, FilterProperties filterProperties) {
        super(type);
        this.filterProperties = Objects.requireNonNull(filterProperties);
    }


    @Override
    public void customizeSafe(T registry) {
        this.addEnabledFilter(registry);
        this.addRenameFilter(registry);
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
    
    // Add to the specified registry a filter which renames meters using provided rename patterns.
    private void addRenameFilter(T registry) {
        Map<String, String> rename = this.filterProperties.getRename();
        MeterFilter renameFilter = new RenameFilter(rename);
        registry.config().meterFilter(renameFilter);
    }
}

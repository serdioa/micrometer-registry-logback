package de.serdioa.spring.configure.metrics.filter;

import java.util.Map;
import java.util.function.Predicate;

import de.serdioa.spring.configure.properties.HierarchicalProperties;
import io.micrometer.core.instrument.Meter;


/**
 * A predicate which returns {@code true} if the specified {@code meterId} shall be denied (excluded) by the filter.
 * <p>
 * The name of the specified {@code meterId} is matched against configured rules, using the longest prefix. If the name
 * does not match any rules, this predicate returns {@code false}, that is do not deny the {@code meterId}.
 */
class PropertiesMeterIdDenyPredicate implements Predicate<Meter.Id> {

    private final HierarchicalProperties<Boolean> enabled;


    public PropertiesMeterIdDenyPredicate(Map<String, Boolean> enabled) {
        this.enabled = new HierarchicalProperties<>(enabled);
    }


    @Override
    public boolean test(Meter.Id meterId) {
        String name = meterId.getName();
        
        // Invert the boolean value because this is a "deny" predicate which shall return "true" to disable a meter.
        // By default return "false", i.e. enable a meter.
        return !this.enabled.getHierarchical(name, Boolean.FALSE);
    }
}

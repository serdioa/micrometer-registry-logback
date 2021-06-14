package de.serdioa.spring.configure.metrics.filter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import io.micrometer.core.instrument.Meter;


/**
 * A predicate which returns {@code true} if the specified {@code meterId} shall be denied (excluded) by the filter.
 * <p>
 * The name of the specified {@code meterId} is matched against configured rules, using the longest prefix. If the name
 * does not match any rules, this predicate returns {@code false}, that is do not deny the {@code meterId}.
 */
class PropertiesMeterIdDenyPredicate implements Predicate<Meter.Id> {

    private final Map<String, Boolean> enabled;


    public PropertiesMeterIdDenyPredicate(Map<String, Boolean> enabled) {
        this.enabled = Objects.requireNonNull(enabled);
    }


    @Override
    public boolean test(Meter.Id meterId) {
        String name = meterId.getName();

        while (name != null && !name.isEmpty()) {
            Boolean enabled = this.enabled.get(name);
            if (enabled != null) {
                // Invert the configured value: if "enabled=true" is configured, return "false", that is "deny".
                return !enabled;
            }

            // Try the prefix.
            int lastSeparatorIndex = name.lastIndexOf('.');
            name = (lastSeparatorIndex > 0 ? name.substring(0, lastSeparatorIndex) : "");
        }

        // Not found the name or any prefix of it in the configuration.
        return false;
    }
}

package de.serdioa.micrometer.pull.console;

import java.util.stream.Collectors;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;


public class ConsoleNameMapper implements HierarchicalNameMapper {

    public static final ConsoleNameMapper DEFAULT = new ConsoleNameMapper();


    @Override
    public String toHierarchicalName(Meter.Id id, NamingConvention convention) {
        return id.getConventionName(convention) + id.getConventionTags(convention).stream()
                .map(t -> " (" + t.getKey() + ": " + t.getValue() + ")")
                .collect(Collectors.joining());
    }
}

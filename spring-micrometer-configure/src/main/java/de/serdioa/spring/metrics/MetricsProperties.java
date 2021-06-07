package de.serdioa.spring.metrics;

import java.util.Map;

import de.serdioa.spring.properties.HierarchicalProperties;
import lombok.Value;


/**
 * Configuration properties for configuring Micrometer metrics.
 */
@Value
public class MetricsProperties {

    private final HierarchicalProperties<Boolean> enable;

    private final HierarchicalProperties<String> tags;


    public MetricsProperties(Map<String, Boolean> enable, Map<String, String> tags) {
        this.enable = new HierarchicalProperties<>(enable);
        this.tags = new HierarchicalProperties<>(tags);
    }
}

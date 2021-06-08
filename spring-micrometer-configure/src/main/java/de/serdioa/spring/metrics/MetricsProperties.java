package de.serdioa.spring.metrics;

import java.util.Map;

import de.serdioa.spring.properties.HierarchicalProperties;
import lombok.Value;


/**
 * Configuration properties for Micrometer metrics.
 */
@Value
public class MetricsProperties {

    /**
     * Indicates whether a particular meter should publish at all.
     */
    private final HierarchicalProperties<Boolean> enable;

    /**
     * Common tags added to all meters.
     */
    private final HierarchicalProperties<String> tags;

    /**
     * Indicates whether a particular meter should publish percentile histograms.
     */
    private HierarchicalProperties<Boolean> percentilesHistogram;
    
    /**
     * Specific percentiles to publish for a particular meter.
     */
    private HierarchicalProperties<double []> percentiles;

    public MetricsProperties(Map<String, Boolean> enable, Map<String, String> tags,
            Map<String, Boolean> percentilesHistogram, Map<String, double []> percentiles) {
        this.enable = new HierarchicalProperties<>(enable);
        this.tags = new HierarchicalProperties<>(tags);
        this.percentilesHistogram = new HierarchicalProperties<>(percentilesHistogram);
        this.percentiles = new HierarchicalProperties<>(percentiles);
    }
}

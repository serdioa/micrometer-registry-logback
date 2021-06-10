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
    private HierarchicalProperties<double[]> percentiles;

    /**
     * A minimum expected value of a particular meter which supports distribution. The value is used to automatically
     * configure the histogram buckets. All values less then the minimum expected are put into the bucket labelled with
     * the minimum expected value.
     */
    private HierarchicalProperties<MeterValue> minimumExpectedValue;

    /**
     * A maximum expected value of a particular meter which supports distribution. The value is used to automatically
     * configure the histogram buckets. All values greater then the maximum expected are put into the bucket labelled
     * with the maximum expected value.
     */
    private HierarchicalProperties<MeterValue> maximumExpectedValue;


    public MetricsProperties(Map<String, Boolean> enable, Map<String, String> tags,
            Map<String, Boolean> percentilesHistogram, Map<String, double[]> percentiles,
            Map<String, MeterValue> minimumExpectedValue, Map<String, MeterValue> maximumExpectedValue) {
        this.enable = new HierarchicalProperties<>(enable);
        this.tags = new HierarchicalProperties<>(tags);
        this.percentilesHistogram = new HierarchicalProperties<>(percentilesHistogram);
        this.percentiles = new HierarchicalProperties<>(percentiles);
        this.minimumExpectedValue = new HierarchicalProperties<>(minimumExpectedValue);
        this.maximumExpectedValue = new HierarchicalProperties<>(maximumExpectedValue);
    }
}

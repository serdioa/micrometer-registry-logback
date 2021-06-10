package de.serdioa.spring.metrics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;


/**
 * A Micrometer {@link MeterFilter} to apply configured properties to metrics.
 */
public class MetricsPropertiesFilter implements MeterFilter {

    private final MetricsProperties properties;

    private final MeterFilter filter;


    public MetricsPropertiesFilter(MetricsProperties properties) {
        this.properties = Objects.requireNonNull(properties, "properties is null");

        this.filter = buildCommonTagsFilter(properties.getTags());
    }


    private static MeterFilter buildCommonTagsFilter(final Map<String, String> tags) {
        if (tags.isEmpty()) {
            // There are no common tags to apply, return a no-op filter.
            return new MeterFilter() {
            };
        }

        // Create a meter filter which applies all common tags.
        final List<Tag> commonTags = tags.entrySet().stream()
                .map((entry) -> Tag.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return MeterFilter.commonTags(commonTags);
    }


    @Override
    public MeterFilterReply accept(Meter.Id id) {
        boolean enabled = this.properties.getEnable().getHierarchical(id.getName(), true);
        return (enabled ? MeterFilterReply.NEUTRAL : MeterFilterReply.DENY);
    }


    @Override
    public Meter.Id map(Meter.Id id) {
        // Add common tags to the specified meter ID.
        return this.filter.map(id);
    }


    @Override
    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
        DistributionStatisticConfig.Builder configBuilder = DistributionStatisticConfig.builder();

        String meterName = id.getName();

        Optional<Boolean> percentilesHistogram = this.properties.getPercentilesHistogram().getHierarchical(meterName);
        if (percentilesHistogram.isPresent()) {
            configBuilder.percentilesHistogram(percentilesHistogram.get());
        }

        Optional<double[]> percentiles = this.properties.getPercentiles().getHierarchical(meterName);
        if (percentiles.isPresent()) {
            configBuilder.percentiles(percentiles.get());
        }

        Optional<MeterValue> minimumExpectedValue =
                this.properties.getMinimumExpectedValue().getHierarchical(meterName);
        if (minimumExpectedValue.isPresent()) {
            Long value = minimumExpectedValue.get().getValue(id.getType());
            if (value != null) {
                configBuilder.minimumExpectedValue(value.doubleValue());
            }
        }

        Optional<MeterValue> maximumExpectedValue =
                this.properties.getMaximumExpectedValue().getHierarchical(meterName);
        if (maximumExpectedValue.isPresent()) {
            Long value = maximumExpectedValue.get().getValue(id.getType());
            if (value != null) {
                configBuilder.maximumExpectedValue(value.doubleValue());
            }
        }

        // The distribution configuration we just build has a priority over defaults provided to this method.
        return configBuilder.build().merge(config);
    }
}

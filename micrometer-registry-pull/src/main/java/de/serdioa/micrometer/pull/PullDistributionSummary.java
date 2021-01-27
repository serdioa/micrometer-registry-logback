package de.serdioa.micrometer.pull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.step.StepDistributionSummary;


public class PullDistributionSummary extends StepDistributionSummary implements PullMeter {

    private final PullHistogramMeasurements histogramMeasurements;

    private final Iterable<PullMeasurement> measurements;


    public PullDistributionSummary(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
            double scale, long stepMillis, boolean supportsAggregablePercentiles) {
        super(id, clock, distributionStatisticConfig, scale, stepMillis, supportsAggregablePercentiles);

        this.histogramMeasurements = new PullHistogramMeasurements(clock, (long) (stepMillis * 0.5), this::takeSnapshot);

        final List<PullMeasurement> measurementsBuilder = new ArrayList<>();
        measurementsBuilder.add(PullMeasurement.of(id.getType(), PullMeasurement.Type.COUNT,
                () -> this.histogramMeasurements.count()));
        measurementsBuilder.add(PullMeasurement.of(id.getType(), PullMeasurement.Type.MEAN,
                () -> this.histogramMeasurements.mean()));
        measurementsBuilder.add(PullMeasurement.of(id.getType(), PullMeasurement.Type.MAX,
                () -> this.histogramMeasurements.max()));

        // All Micrometer implementations of histograms keep the same indices in returned histograms as in the
        // configuration. To speed up, we could access histogram values by indices, instead of searching them
        // by values.
        if (distributionStatisticConfig.isPublishingPercentiles()) {
            double[] percentiles = distributionStatisticConfig.getPercentiles();
            for (int i = 0; i < percentiles.length; ++i) {
                // Lambda expression below requires final index variable.
                final int percentileIndex = i;
                double percentile = percentiles[i];
                measurementsBuilder.add(PullMeasurement.of(id.getType(), PullMeasurement.Type.PERCENTILE, percentile,
                        () -> this.histogramMeasurements.percentile(percentileIndex)));
            }
        }
        if (distributionStatisticConfig.isPublishingHistogram()) {
            Set<Double> histogramBuckets = distributionStatisticConfig
                    .getHistogramBuckets(supportsAggregablePercentiles);

            int i = 0;
            for (Double histogramBucket : histogramBuckets) {
                // Lambda expression below requires final index variable.
                final int histogramIndex = i++;
                measurementsBuilder.add(PullMeasurement.of(id.getType(), PullMeasurement.Type.HISTOGRAM,
                        histogramBucket, () -> this.histogramMeasurements.histogram(histogramIndex)));
            }
        }

        this.measurements = Collections.unmodifiableList(measurementsBuilder);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }
}

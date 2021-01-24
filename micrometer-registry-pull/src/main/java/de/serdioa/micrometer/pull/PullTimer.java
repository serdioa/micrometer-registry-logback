package de.serdioa.micrometer.pull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.TimeUtils;


public class PullTimer extends StepTimer implements PullMeter {

    public static final String COUNT = "count";
    public static final String MEAN = "mean";
    public static final String MAX = "max";

    public static final String PERCENTILE_TEMPLATE = "%s%%";
    public static final String HISTOGRAM_TEMPLATE = "SLA %s";

    private final PullHistogramMeasurements histogramMeasurements;

    private final List<PullMeasurement> measurements;


    public PullTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig,
            PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis,
            boolean supportsAggregablePercentiles) {
        super(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepMillis,
                supportsAggregablePercentiles);

        this.histogramMeasurements = new PullHistogramMeasurements(clock, (long) (stepMillis * 0.5), this::takeSnapshot);

        final TimeUnit timeUnit = this.baseTimeUnit();

        final List<PullMeasurement> measurements = new ArrayList<>();
        measurements.add(new PullMeasurement(COUNT, () -> this.histogramMeasurements.count()));
        measurements.add(new PullMeasurement(MEAN, () -> this.histogramMeasurements.mean(timeUnit)));
        measurements.add(new PullMeasurement(MAX, () -> this.histogramMeasurements.max(timeUnit)));

        // All Micrometer implementations of histograms keep the same indices in returned histograms as in the
        // configuration. To speed up, we could access histogram values by indices, instead of searching them
        // by values.
        if (distributionStatisticConfig.isPublishingPercentiles()) {
            double[] percentiles = distributionStatisticConfig.getPercentiles();
            for (int i = 0; i < percentiles.length; ++i) {
                // Lambda expression below requires final index variable.
                final int percentileIndex = i;
                double percentile = percentiles[i];
                measurements.add(new PullMeasurement(this.getPercentileMeasurementName(percentile),
                        () -> this.histogramMeasurements.percentile(percentileIndex, timeUnit)));
            }
        }
        if (distributionStatisticConfig.isPublishingHistogram()) {
            Set<Double> histogramBuckets = distributionStatisticConfig
                    .getHistogramBuckets(supportsAggregablePercentiles);

            int i = 0;
            for (Double histogramBucketNanoseconds : histogramBuckets) {
                double histogramBucket = TimeUtils.nanosToUnit(histogramBucketNanoseconds, timeUnit);

                // Lambda expression below requires final index variable.
                final int histogramIndex = i++;
                measurements.add(new PullMeasurement(this.getHistogramMeasurementName(histogramBucket),
                        () -> this.histogramMeasurements.histogram(histogramIndex)));
            }
        }

        this.measurements = Collections.unmodifiableList(measurements);
    }


    @Override
    public Iterable<PullMeasurement> getPullMeasurements() {
        return this.measurements;
    }


    protected String getPercentileMeasurementName(double percentile) {
        return String.format(PERCENTILE_TEMPLATE, percentile * 100);
    }


    protected String getHistogramMeasurementName(double histogramBucket) {
        return String.format(HISTOGRAM_TEMPLATE, histogramBucket);
    }
}

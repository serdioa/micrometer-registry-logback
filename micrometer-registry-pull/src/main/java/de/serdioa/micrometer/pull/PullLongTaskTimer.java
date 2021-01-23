package de.serdioa.micrometer.pull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import io.micrometer.core.instrument.util.TimeUtils;


public class PullLongTaskTimer extends DefaultLongTaskTimer implements PullMeter {

    public static final String COUNT = "count";
    public static final String MEAN = "mean";
    public static final String MAX = "max";

    public static final String PERCENTILE_TEMPLATE = "%s%%";
    public static final String HISTOGRAM_TEMPLATE = "SLA %s";

    private final Clock clock;
    private final long stepMillis;
    private final AtomicReference<StampedReference<HistogramSnapshot>> snapshotRef = new AtomicReference<>();

    private final List<PullMeasurement> measurements;


    public PullLongTaskTimer(Meter.Id id, Clock clock, TimeUnit baseTimeUnit, long stepMillis,
            DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles) {
        super(id, clock, baseTimeUnit, distributionStatisticConfig, supportsAggregablePercentiles);

        this.clock = Objects.requireNonNull(clock, "clock is required");
        this.stepMillis = stepMillis;

        final List<PullMeasurement> measurements = new ArrayList<>();
        measurements.add(new PullMeasurement(COUNT, this::getCountFromSnapshot));
        measurements.add(new PullMeasurement(MEAN, this::getMeanFromSnapshot));
        measurements.add(new PullMeasurement(MAX, this::getMaxFromSnapshot));

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
                        () -> this.getPercentileValue(percentileIndex)));
            }
        }
        if (distributionStatisticConfig.isPublishingHistogram()) {
            Set<Double> histogramBuckets = distributionStatisticConfig
                    .getHistogramBuckets(supportsAggregablePercentiles);

            int i = 0;
            for (Double histogramBucketNanoseconds : histogramBuckets) {
                double histogramBucket = TimeUtils.nanosToUnit(histogramBucketNanoseconds, this.baseTimeUnit());

                // Lambda expression below requires final index variable.
                final int histogramIndex = i++;
                measurements.add(new PullMeasurement(this.getHistogramMeasurementName(histogramBucket),
                        () -> this.getHistogramValue(histogramIndex)));
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


    private HistogramSnapshot getSnapshot() {
        StampedReference<HistogramSnapshot> stampedSnapshot = this.snapshotRef.get();
        boolean refresh;
        if (stampedSnapshot == null) {
            refresh = true;
        } else {
            // Our goal is to try to publish all measurements within one iteration from the same histogram snapshot.
            // How often shall we re-read the histogram snapshot from the data source?
            //
            // The pulling metrics collector is not absolutely precise, sometimes it may pull slightly earlier or
            // later. If it pulls slightly earlier, but we wait exactly stepMillis between re-reads, than we would
            // populate some measurements from the previous snapshot (because it has not expired yet), and some
            // from the new snapshot (because the old snapshot expired just now).
            //
            // To reduce a chance of such inconsistency, we set the threshold to 0.5 of the stepMillis.
            // This leads to best results: if the pulling metrics collector is scheduled to collect metrics every
            // stepMillis, practically everytime it waits between runs more than 0.5 * stepMillis, and practically
            // everytime it takes less than 0.5 * stepMillis to collect all metrics from the same indicator.
            //
            // Exceptions are possible in extreme cases (either stepMillis = 1 ms, or due to a very long GC pause
            // the polling metrics collector took more than 0.5 * stepMillis to collect all metrics from the same
            // indicator), but we expect them to be so seldom, that we do not care.

            long timeSinceLastRefreshMillis = clock.wallTime() - stampedSnapshot.getStamp();
            refresh = (timeSinceLastRefreshMillis > this.stepMillis * 0.5);
        }

        if (refresh) {
            long timestamp = clock.wallTime();
            HistogramSnapshot newSnapshot = this.takeSnapshot();
            StampedReference<HistogramSnapshot> newStampedSnapshot = new StampedReference<>(timestamp, newSnapshot);

            // We do not check result of compareAndSet: if a concurrent thread updated the cached snapshot,
            // that's OK. We still use the most recent snapshot we just took.
            snapshotRef.compareAndSet(stampedSnapshot, newStampedSnapshot);
            return newSnapshot;
        } else {
            return stampedSnapshot.getReference();
        }
    }


    private double getCountFromSnapshot() {
        HistogramSnapshot snapshot = this.getSnapshot();
        return snapshot.count();
    }


    private double getMeanFromSnapshot() {
        HistogramSnapshot snapshot = this.getSnapshot();
        return snapshot.mean(this.baseTimeUnit());
    }


    private double getMaxFromSnapshot() {
        HistogramSnapshot snapshot = this.getSnapshot();
        return snapshot.max(this.baseTimeUnit());
    }


    private Double getPercentileValue(int index) {
        HistogramSnapshot snapshot = this.getSnapshot();

        // Get the percentile value by index, but with a sanity check.
        ValueAtPercentile[] percentileValues = snapshot.percentileValues();
        if (percentileValues != null && percentileValues.length > index) {
            return percentileValues[index].value(this.baseTimeUnit());
        } else {
            return Double.NaN;
        }
    }


    private Double getHistogramValue(int index) {
        HistogramSnapshot snapshot = this.getSnapshot();

        // Get the histogram value by index, but with a sanity check.
        CountAtBucket[] histogramCounts = snapshot.histogramCounts();
        if (histogramCounts != null && histogramCounts.length > index) {
            return histogramCounts[index].count();
        } else {
            return Double.NaN;
        }
    }
}

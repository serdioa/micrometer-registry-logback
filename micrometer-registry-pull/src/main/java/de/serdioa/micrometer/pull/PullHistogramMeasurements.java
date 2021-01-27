package de.serdioa.micrometer.pull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import lombok.AllArgsConstructor;
import lombok.ToString;


/**
 * Provides measurements derived from a {@link HistogramSnapshot}, such as percentiles. The polling metrics collector
 * often polls measurements one-by-one. To improve performance, this class reads a new histogram snapshot from the
 * source not each call, but only after a configured timeout.
 * <p>
 * Our goal is to try to publish all measurements within one iteration from the same histogram snapshot. The polling
 * metrics collector is not absolutely precise, sometime it may poll slightly earlier or later. If it polls earlier as
 * expected, but we wait exactly the time period we normally expect between polls, we would populate several first
 * measurements from the previous snapshot because it has not expired yet, and later measurements from the new snapshot,
 * taken once the old snapshot expired.
 * <p>
 * To reduce a chance of such inconsistency, it is recommended to set the time interval for an expiration of histogram
 * snapshots to 0.5 of the time period normally expected between the polling calls. This leads to best results: if the
 * polling metrics collector is scheduled to poll metrics every N seconds, practically in all cases it waits between
 * polls for more than 0.5 * N seconds (so the previous snapshot is already expired, and the new one is taken when
 * polling the first measurement in a particular run), and practically every time it takes less than 0.5 * N to collect
 * all measurements (so all measurements in the same run are based on the same histogram snapshot, and are mutually
 * consistent).
 * <p>
 * Exceptions are possible in extreme cases, for example when the polling metrics collector polls metrics every
 * millisecond, or when due to a very long GC pause the metrics collector takes more than 0.5 * N to collect all
 * measurements in the same run), but such cases expected to be very seldom.
 */
public class PullHistogramMeasurements {

    // The clock used to measure elapsed time.
    private final Clock clock;

    // The time interval in milliseconds to keep a histogram.
    private final long stepMillis;

    // The function to read histograms from the source.
    private final Supplier<HistogramSnapshot> histogramSupplier;

    // An atomic holder for histogram snapshots, together with a timestamp when the snapshot was taken.
    private final AtomicReference<StampedReference<HistogramSnapshot>> snapshotRef = new AtomicReference<>();


    public PullHistogramMeasurements(Clock clock, long stepMillis, Supplier<HistogramSnapshot> histogramSupplier) {
        this.clock = Objects.requireNonNull(clock, "clock is required");
        this.stepMillis = stepMillis;
        this.histogramSupplier = Objects.requireNonNull(histogramSupplier, "histogramSupplier is required");
    }


    public double count() {
        return this.getSnapshot().count();
    }


    public double mean() {
        return this.getSnapshot().mean();
    }


    public double mean(TimeUnit unit) {
        return this.getSnapshot().mean(unit);
    }


    public double max() {
        return this.getSnapshot().max();
    }


    public double max(TimeUnit unit) {
        return this.getSnapshot().max(unit);
    }


    public double percentile(int index) {
        ValueAtPercentile value = this.valueAtPercentile(index);
        return (value != null ? value.value() : Double.NaN);
    }


    public double percentile(int index, TimeUnit unit) {
        ValueAtPercentile value = this.valueAtPercentile(index);
        return (value != null ? value.value(unit) : Double.NaN);
    }


    private ValueAtPercentile valueAtPercentile(int index) {
        HistogramSnapshot snapshot = this.getSnapshot();

        // Get the percentile value by index, but with a sanity check.
        ValueAtPercentile[] percentileValues = snapshot.percentileValues();
        if (percentileValues != null && percentileValues.length > index) {
            return percentileValues[index];
        } else {
            return null;
        }
    }


    public double histogram(int index) {
        HistogramSnapshot snapshot = this.getSnapshot();

        // Get the histogram value by index, but with a sanity check.
        CountAtBucket[] histogramCounts = snapshot.histogramCounts();
        if (histogramCounts != null && histogramCounts.length > index) {
            return histogramCounts[index].count();
        } else {
            return Double.NaN;
        }
    }


    // Returns a histogram snapshot, using the configured expiration duration to decide whether a cached snapshot
    // may be reused, or a new one shall be taken.
    private HistogramSnapshot getSnapshot() {
        StampedReference<HistogramSnapshot> stampedSnapshot = this.snapshotRef.get();
        boolean refresh;
        if (stampedSnapshot == null) {
            // There is no cached histogram yet, we have to read one from the supplier.
            refresh = true;
        } else {
            // Check whether the cached histogram already expired.
            long timeSinceLastRefreshMillis = clock.wallTime() - stampedSnapshot.stamp;
            refresh = (timeSinceLastRefreshMillis > this.stepMillis);
        }

        // Shall we read a new histogram, or may we reuse the cached one?
        if (refresh) {
            // Read and cache the new histogram.

            long timestamp = clock.wallTime();
            HistogramSnapshot newSnapshot = this.histogramSupplier.get();
            StampedReference<HistogramSnapshot> newStampedSnapshot = new StampedReference<>(timestamp, newSnapshot);

            // We do not check result of compareAndSet: if a concurrent thread updated the cached snapshot,
            // that's OK. We still use the most recent snapshot we just took.
            snapshotRef.compareAndSet(stampedSnapshot, newStampedSnapshot);
            return newSnapshot;
        } else {
            // Reuse the cached histogram.
            return stampedSnapshot.reference;
        }
    }


    // A holder for an object and a timestamp.
    @ToString
    @AllArgsConstructor
    private static class StampedReference<T> {

        private final long stamp;
        private final T reference;
    }
}

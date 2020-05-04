package de.serdioa.micrometer.logging.direct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.util.MeterEquivalence;
import io.micrometer.core.instrument.util.TimeUtils;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class DirectLoggingLongTaskTimer extends AbstractMeter implements LongTaskTimer {

    private final AtomicLong nextTask = new AtomicLong(0L);

    private final Deque<SampleImpl> activeTasks = new ConcurrentLinkedDeque<>();

    private final Clock clock;
    private final TimeUnit baseTimeUnit;
    private final DistributionStatisticConfig distributionStatisticConfig;
    private final boolean supportsAggregablePercentiles;

    private final Logger logger;
    private final Marker tags;


    public DirectLoggingLongTaskTimer(Id id, Clock clock, TimeUnit baseTimeUnit, DistributionStatisticConfig distributionStatisticConfig,
            boolean supportsAggregablePercentiles, Logger logger, Marker tags) {
        super(id);
        this.clock = clock;
        this.baseTimeUnit = baseTimeUnit;
        this.distributionStatisticConfig = distributionStatisticConfig;
        this.supportsAggregablePercentiles = supportsAggregablePercentiles;

        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }


    @Override
    public Sample start() {
        long task = this.nextTask.getAndIncrement();

        SampleImpl sample = new SampleImpl(task, this::onStop);
        activeTasks.add(sample);

        if (this.logger.isInfoEnabled()) {
            this.logger.info(this.tags, null, new StartEvent(task));
        }

        return sample;
    }


    private void onStop(long task, long duration) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(this.tags, null, new StopEvent(task, duration));
        }
    }


    @Override
    public double duration(TimeUnit unit) {
        long now = clock.monotonicTime();
        long sum = 0L;
        for (SampleImpl task : activeTasks) {
            sum += now - task.startTime();
        }
        return TimeUtils.nanosToUnit(sum, unit);
    }


    @Override
    public double max(TimeUnit unit) {
        Sample oldest = activeTasks.peek();
        return oldest == null ? 0.0 : oldest.duration(unit);
    }


    @Override
    public int activeTasks() {
        return activeTasks.size();
    }


    @Override
    public TimeUnit baseTimeUnit() {
        return baseTimeUnit;
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return MeterEquivalence.equals(this, o);
    }


    @Override
    public int hashCode() {
        return MeterEquivalence.hashCode(this);
    }


    @Override
    public HistogramSnapshot takeSnapshot() {
        Queue<Double> percentilesRequested =
                new ArrayBlockingQueue<>(distributionStatisticConfig.getPercentiles() == null ? 1
                        : distributionStatisticConfig.getPercentiles().length);
        double[] percentilesRequestedArr = distributionStatisticConfig.getPercentiles();
        if (percentilesRequestedArr != null && percentilesRequestedArr.length > 0) {
            Arrays.stream(percentilesRequestedArr).sorted().boxed().forEach(percentilesRequested::add);
        }

        NavigableSet<Double> buckets = distributionStatisticConfig.getHistogramBuckets(supportsAggregablePercentiles);

        CountAtBucket[] countAtBucketsArr = new CountAtBucket[0];

        List<Double> percentilesAboveInterpolatableLine = percentilesRequested.stream()
                .filter(p -> p * (activeTasks.size() + 1) > activeTasks.size())
                .collect(Collectors.toList());

        percentilesRequested.removeAll(percentilesAboveInterpolatableLine);

        List<ValueAtPercentile> valueAtPercentiles = new ArrayList<>(percentilesRequested.size());

        if (!percentilesRequested.isEmpty() || !buckets.isEmpty()) {
            Double percentile = percentilesRequested.poll();
            Double bucket = buckets.pollFirst();

            List<CountAtBucket> countAtBuckets = new ArrayList<>(buckets.size());

            SampleImpl priorActiveTask = null;
            int i = 0;

            Iterator<SampleImpl> youngestToOldest = activeTasks.descendingIterator();
            while (youngestToOldest.hasNext()) {
                SampleImpl activeTask = youngestToOldest.next();
                i++;
                if (bucket != null) {
                    if (activeTask.duration(TimeUnit.NANOSECONDS) > bucket) {
                        countAtBuckets.add(new CountAtBucket(bucket, i - 1));
                        bucket = buckets.pollFirst();
                    }
                }

                if (percentile != null) {
                    double rank = percentile * (activeTasks.size() + 1);

                    if (i >= rank) {
                        double percentileValue = activeTask.duration(TimeUnit.NANOSECONDS);
                        if (i != rank && priorActiveTask != null) {
                            // interpolate the percentile value when the active task rank is non-integral
                            double priorPercentileValue = priorActiveTask.duration(TimeUnit.NANOSECONDS);
                            percentileValue = priorPercentileValue
                                    + ((percentileValue - priorPercentileValue) * (rank - (int) rank));
                        }

                        valueAtPercentiles.add(new ValueAtPercentile(percentile, percentileValue));
                        percentile = percentilesRequested.poll();
                    }
                }

                priorActiveTask = activeTask;
            }

            // fill out the rest of the cumulative histogram
            while (bucket != null) {
                countAtBuckets.add(new CountAtBucket(bucket, i));
                bucket = buckets.pollFirst();
            }

            countAtBucketsArr = countAtBuckets.toArray(countAtBucketsArr);
        }

        double duration = duration(TimeUnit.NANOSECONDS);
        double max = max(TimeUnit.NANOSECONDS);

        // we wouldn't need to iterate over all the active tasks just to calculate the 100th percentile, which is just the max.
        for (Double percentile : percentilesAboveInterpolatableLine) {
            valueAtPercentiles.add(new ValueAtPercentile(percentile, max));
        }

        ValueAtPercentile[] valueAtPercentilesArr = valueAtPercentiles.toArray(new ValueAtPercentile[0]);

        return new HistogramSnapshot(
                activeTasks.size(),
                duration,
                max,
                valueAtPercentilesArr,
                countAtBucketsArr,
                (ps, scaling) -> ps.print("Summary output for LongTaskTimer histograms is not supported.")
        );
    }


    @FunctionalInterface
    private interface OnStopCallback {

        void onStop(long task, long duration);
    }


    class SampleImpl extends Sample {

        private final long task;
        private final OnStopCallback onStop;
        private final long startTime;
        private volatile boolean stopped = false;


        private SampleImpl(long task, OnStopCallback onStop) {
            this.task = task;
            this.onStop = Objects.requireNonNull(onStop);
            this.startTime = clock.monotonicTime();
        }


        @Override
        public long stop() {
            activeTasks.remove(this);
            long duration = (long) duration(TimeUnit.NANOSECONDS);
            stopped = true;

            this.onStop.onStop(this.task, duration);

            return duration;
        }


        @Override
        public double duration(TimeUnit unit) {
            return stopped ? -1 : TimeUtils.nanosToUnit(clock.monotonicTime() - startTime, unit);
        }


        private long startTime() {
            return startTime;
        }


        @Override
        public String toString() {
            double durationInNanoseconds = duration(TimeUnit.NANOSECONDS);
            return "SampleImpl{"
                    + "duration(seconds)=" + TimeUtils.nanosToUnit(durationInNanoseconds, TimeUnit.SECONDS) + ", "
                    + "duration(nanos)=" + durationInNanoseconds + ", "
                    + "startTimeNanos=" + startTime
                    + '}';
        }
    }


    private static class StartEvent implements StructuredArgument {

        private final long task;


        public StartEvent(long task) {
            this.task = task;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("t", "ltt");
            generator.writeStringField("ev", "start");
            generator.writeNumberField("task", this.task);
        }
    }


    private static class StopEvent implements StructuredArgument {

        private final long task;
        private final long amount;


        public StopEvent(long task, long amount) {
            this.task = task;
            this.amount = amount;
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            generator.writeStringField("t", "ltt");
            generator.writeStringField("ev", "stop");
            generator.writeNumberField("task", this.task);
            generator.writeNumberField("amt", this.amount);
        }
    }
}

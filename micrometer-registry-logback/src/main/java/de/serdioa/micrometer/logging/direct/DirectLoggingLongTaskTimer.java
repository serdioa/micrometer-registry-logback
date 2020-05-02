package de.serdioa.micrometer.logging.direct;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonGenerator;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.util.MeterEquivalence;
import io.micrometer.core.instrument.util.TimeUtils;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class DirectLoggingLongTaskTimer extends AbstractMeter implements LongTaskTimer {

    private final ConcurrentMap<Long, Long> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextTask = new AtomicLong(0L);
    private final Clock clock;

    private final Logger logger;
    private final Marker tags;


    public DirectLoggingLongTaskTimer(Id id, Clock clock, Logger logger, Marker tags) {
        super(id);

        this.clock = clock;
        this.logger = Objects.requireNonNull(logger);
        this.tags = tags;
    }


    @Override
    public Sample start() {
        long task = this.nextTask.getAndIncrement();
        this.tasks.put(task, this.clock.monotonicTime());

        if (this.logger.isInfoEnabled()) {
            this.logger.info(this.tags, null, new StartEvent(task));
        }

        return new Sample(this, task);
    }


    @Override
    public long stop(long task) {
        Long startTime = this.tasks.remove(task);
        long amount = (startTime != null ? this.clock.monotonicTime() - startTime : -1);

        if (this.logger.isInfoEnabled()) {
            this.logger.info(this.tags, null, new StopEvent(task, amount));
        }

        return amount;
    }


    @Override
    public double duration(long task, TimeUnit unit) {
        Long startTime = this.tasks.get(task);
        return (startTime != null) ? TimeUtils.nanosToUnit(this.clock.monotonicTime() - startTime, unit) : -1L;
    }


    @Override
    public double duration(TimeUnit unit) {
        long now = this.clock.monotonicTime();
        long sum = 0L;
        for (long startTime : this.tasks.values()) {
            sum += now - startTime;
        }
        return TimeUtils.nanosToUnit(sum, unit);
    }


    @Override
    public int activeTasks() {
        return this.tasks.size();
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

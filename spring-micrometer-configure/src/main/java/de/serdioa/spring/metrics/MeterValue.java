package de.serdioa.spring.metrics;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Meter;
import org.springframework.boot.convert.DurationStyle;


/**
 * A value configured as an expected meter boundary, or as a SLA. A value may be a {@link Long} or a {@link Duration}
 * (latter only for timers). When a numeric value is specified for a timer, it is considered to be milliseconds.
 */
public class MeterValue {

    // The underlying value either as a Long or as a Duration.
    private final Object value;


    private MeterValue(long value) {
        this.value = value;
    }


    private MeterValue(Duration value) {
        this.value = Objects.requireNonNull(value);
    }


    public static MeterValue of(long value) {
        return new MeterValue(value);
    }


    public static MeterValue of(Duration value) {
        return new MeterValue(value);
    }


    public static MeterValue of(String value) {
        if (isNumeric(value)) {
            long longValue = Long.parseLong(value);
            return MeterValue.of(longValue);
        } else {
            Duration durationValue = DurationStyle.detectAndParse(value);
            return MeterValue.of(durationValue);
        }
    }


    private static boolean isNumeric(String value) {
        // In our context (configuring Micrometer) only non-negative numbers makes sense.
        return value.codePoints().allMatch(Character::isDigit);
    }


    public Long getDistributionSummaryValue() {
        if (this.value instanceof Long) {
            return (Long) this.value;
        } else {
            return null;
        }
    }


    public Long getTimerValue() {
        if (this.value instanceof Long) {
            // Assume value is specified in milliseconds. Transform it to nanoseconds used by Micrometer.
            return TimeUnit.MILLISECONDS.toNanos((long) this.value);
        } else {
            // Constructors ensure that this.value != null.
            return ((Duration) this.value).toNanos();
        }
    }


    public Long getValue(Meter.Type meterType) {
        if (meterType == Meter.Type.TIMER || meterType == Meter.Type.LONG_TASK_TIMER) {
            return this.getTimerValue();
        } else {
            return this.getDistributionSummaryValue();
        }
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.util.TimeUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.Marker;


/* package private */ class LoggingTimeGauge<T> extends LoggingGauge<T> implements TimeGauge {

    @Getter
    private final TimeUnit baseTimeUnit;


    public LoggingTimeGauge(Meter.Id id, T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction,
            TimeUnit baseTimeUnit, Logger logger, Marker tags) {
        super(id, obj, source -> TimeUtils.convert(valueFunction.applyAsDouble(source),
                valueFunctionUnit, baseTimeUnit), logger, tags);

        this.baseTimeUnit = Objects.requireNonNull(baseTimeUnit);
    }


    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }
}

package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.Counter;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class CounterSnapshot {
    @Getter
    protected final double count;

    public CounterSnapshot(Counter counter) {
        this.count = counter.count();
    }
}

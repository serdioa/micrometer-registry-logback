package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.FunctionCounter;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class FunctionCounterSnapshot {
    @Getter
    protected final double count;

    public FunctionCounterSnapshot(FunctionCounter counter) {
        this.count = counter.count();
    }
}

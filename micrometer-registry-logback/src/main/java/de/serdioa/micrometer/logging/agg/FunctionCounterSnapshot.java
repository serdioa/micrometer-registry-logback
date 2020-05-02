package de.serdioa.micrometer.logging.agg;

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

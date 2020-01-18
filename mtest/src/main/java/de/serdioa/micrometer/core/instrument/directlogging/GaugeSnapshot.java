package de.serdioa.micrometer.core.instrument.directlogging;

import io.micrometer.core.instrument.Gauge;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class GaugeSnapshot {

    @Getter
    protected double value;


    public GaugeSnapshot(Gauge gauge) {
        this.value = gauge.value();
    }
}

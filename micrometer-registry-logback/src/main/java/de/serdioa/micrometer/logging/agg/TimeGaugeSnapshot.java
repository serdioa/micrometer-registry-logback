package de.serdioa.micrometer.logging.agg;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.TimeGauge;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class TimeGaugeSnapshot extends GaugeSnapshot {

    @Getter
    protected TimeUnit baseTimeUnit;


    public TimeGaugeSnapshot(TimeGauge timeGauge) {
        super(timeGauge);

        this.baseTimeUnit = timeGauge.baseTimeUnit();
    }
}

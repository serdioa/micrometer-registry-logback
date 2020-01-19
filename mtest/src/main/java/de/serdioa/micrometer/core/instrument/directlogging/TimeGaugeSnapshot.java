package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.TimeGauge;
import lombok.Getter;


public class TimeGaugeSnapshot extends GaugeSnapshot {

    @Getter
    protected TimeUnit baseTimeUnit;


    public TimeGaugeSnapshot(TimeGauge timeGauge) {
        super(timeGauge);

        this.baseTimeUnit = timeGauge.baseTimeUnit();
    }
}

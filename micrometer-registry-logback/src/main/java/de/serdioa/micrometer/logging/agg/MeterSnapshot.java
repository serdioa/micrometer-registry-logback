package de.serdioa.micrometer.logging.agg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import lombok.Getter;
import lombok.ToString;


@ToString
/* package private */ class MeterSnapshot {

    @Getter
    protected final Meter.Id id;

    @Getter
    protected final List<String> names;

    @Getter
    protected final List<Double> values;


    public MeterSnapshot(Meter meter) {
        this.id = meter.getId();

        List<String> measurementNames = new ArrayList<>();
        List<Double> measurementValues = new ArrayList<>();

        for (Measurement measurement : meter.measure()) {
            measurementNames.add(measurement.getStatistic().getTagValueRepresentation());
            measurementValues.add(measurement.getValue());
        }

        this.names = Collections.unmodifiableList(measurementNames);
        this.values = Collections.unmodifiableList(measurementValues);
    }
}

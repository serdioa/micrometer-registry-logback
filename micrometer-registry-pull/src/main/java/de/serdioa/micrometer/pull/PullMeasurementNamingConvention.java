package de.serdioa.micrometer.pull;

import io.micrometer.core.instrument.Meter;


public interface PullMeasurementNamingConvention {

    PullMeasurementNamingConvention DEFAULT = (meterType, measurementType, value) -> {
        switch (measurementType) {
            case HISTOGRAM:
                return String.format("SLA %s", value);
            case PERCENTILE:
                return String.format("%s%%", value);
            case TOTAL_TIME:
                return "total time";
            default:
                return measurementType.name().toLowerCase();
        }
    };


    /**
     * Provides a name for the measurement appropriate for a particular monitoring system.
     *
     * @param meterType the type of the meter.
     * @param measurementType the type of the measurement.
     * @param value the value is available only for {@link PullMeasurement.Type#PERCENTILE} and
     * {@link PullMeasurement.Type#HISTOGRAM}.
     *
     * @return the name for the measurement appropriate for a particular monitoring system.
     */
    String name(Meter.Type meterType, PullMeasurement.Type measurementType, Double value);
}

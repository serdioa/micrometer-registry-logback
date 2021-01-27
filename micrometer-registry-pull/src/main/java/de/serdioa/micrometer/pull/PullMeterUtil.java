package de.serdioa.micrometer.pull;

import java.util.Arrays;
import java.util.Collections;


class PullMeterUtil {

    private PullMeterUtil() {
        // A private constructor prevents this class from being instantiated.
    }


    public static Iterable<PullMeasurement> measurements(PullMeasurement... measurements) {
        if (measurements == null || measurements.length == 0) {
            return Collections.emptyList();
        } else if (measurements.length == 1) {
            return Collections.singletonList(measurements[0]);
        } else {
            return Arrays.asList(measurements);
        }
    }
}

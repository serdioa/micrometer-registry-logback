package de.serdioa.spring.metrics;

import java.time.Duration;

import io.micrometer.core.instrument.push.PushMeterRegistry;
import lombok.Data;


/**
 * Base class for properties used to configure a Micrometer {@link PushMeterRegistry}.
 */
@Data
public class PushRegistryProperties {

    /**
     * Returns a {@code boolean} indicating if the publishing is enabled. Defaults to {@code true}.
     *
     * @return {@code true} if the publishing is enabled, {@code false} otherwise.
     */
    private boolean enabled = true;

    /**
     * Returns step size, that is reporting frequency. Defaults to 1 minute.
     *
     * @return the step size, that is reporting frequency.
     */
    private Duration step = Duration.ofMinutes(1);

    /**
     * Returns the number of measurements per request when sending measurements to the back-end. If more measurements
     * are found, then multiple requests will be made. Defaults to {@link 10000}.
     *
     * @return the number of measurements per request when sending measurements to the back-end.
     */
    private int batchSize = 10_000;
}

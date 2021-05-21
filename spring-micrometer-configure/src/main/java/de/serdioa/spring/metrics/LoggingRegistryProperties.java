package de.serdioa.spring.metrics;

import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Properties used to configure a Micrometer {@link LoggingMeterRegistry}.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoggingRegistryProperties extends StepRegistryProperties {

    private String prefix = "metrics";

    /**
     * A boolean indicating whether counters and timers that have no activity in an interval are still logged.
     */
    private boolean logInactive = true;
}

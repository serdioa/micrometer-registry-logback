package de.serdioa.spring.metrics;

import io.micrometer.core.instrument.step.StepMeterRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Base class for properties used to configure a Micrometer {@link StepMeterRegistry}.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StepRegistryProperties extends PushRegistryProperties {
    // This class mirrors structure of Micrometer configuration.
    // No specific configuration properties at the moment.
}

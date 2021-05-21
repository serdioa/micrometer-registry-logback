package de.serdioa.spring.metrics;

import io.micrometer.core.instrument.step.StepRegistryConfig;


/**
 * Adapter which presents configuration properties as Micrometer {@link StepRegistryConfig}.
 *
 * @param <T> the type of configuration properties of Micrometer registry.
 */
public class StepRegistryPropertiesConfigAdapter<T extends StepRegistryProperties>
        extends PushRegistryPropertiesConfigAdapter<T> implements StepRegistryConfig {

    public StepRegistryPropertiesConfigAdapter(T properties) {
        super(properties);
    }
}

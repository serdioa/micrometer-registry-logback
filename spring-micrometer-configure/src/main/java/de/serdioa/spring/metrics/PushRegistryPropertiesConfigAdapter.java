package de.serdioa.spring.metrics;

import java.time.Duration;

import io.micrometer.core.instrument.push.PushRegistryConfig;


/**
 * Adapter which presents configuration properties as Micrometer {@link PushRegistryConfig}.
 *
 * @param <T> the type of configuration properties of Micrometer registry.
 */
public class PushRegistryPropertiesConfigAdapter<T extends PushRegistryProperties> extends PropertiesConfigAdapter<T>
        implements PushRegistryConfig {

    public PushRegistryPropertiesConfigAdapter(T properties) {
        super(properties);
    }


    @Override
    public String prefix() {
        return null;
    }


    @Override
    public String get(String key) {
        return null;
    }


    @Override
    public boolean enabled() {
        return this.get(T::isEnabled, PushRegistryConfig.super::enabled);
    }


    @Override
    public Duration step() {
        return this.get(T::getStep, PushRegistryConfig.super::step);
    }


    @Override
    public int batchSize() {
        return this.get(T::getBatchSize, PushRegistryConfig.super::batchSize);
    }
}

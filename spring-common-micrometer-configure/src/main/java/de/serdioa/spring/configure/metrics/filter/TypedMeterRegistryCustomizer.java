package de.serdioa.spring.configure.metrics.filter;

import java.util.Objects;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;


public abstract class TypedMeterRegistryCustomizer<T extends MeterRegistry> implements MeterRegistryCustomizer<T> {

    private final Class<T> type;


    public TypedMeterRegistryCustomizer(Class<T> type) {
        this.type = Objects.requireNonNull(type);
    }


    @Override
    public final void customize(T registry) {
        if (type.isInstance(registry)) {
            this.customizeSafe(registry);
        }
    }


    public abstract void customizeSafe(T registry);
}

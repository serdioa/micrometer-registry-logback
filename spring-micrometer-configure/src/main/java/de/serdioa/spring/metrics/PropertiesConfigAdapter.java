package de.serdioa.spring.metrics;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Base class for adapters which present configuration properties as an instance of Micrometer registry configuration.
 *
 * This class is based on {@code org.springframework.boot.actuate.autoconfigure.metrics}. It is extracted from the
 * original library to be used in plain Spring, as opposed to the Spring Boot context.
 *
 * @param <T> the type of configuration properties of Micrometer registry.
 */
public class PropertiesConfigAdapter<T> {

    private final T properties;


    public PropertiesConfigAdapter(T properties) {
        this.properties = Objects.requireNonNull(properties);
    }


    protected final <V> V get(Function<T, V> getter, Supplier<V> fallback) {
        V value = getter.apply(this.properties);
        return (value != null ? value : fallback.get());
    }
}

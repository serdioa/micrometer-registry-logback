package de.serdioa.spring.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Properties with hierarchical property names, where the longest match wins.
 * <p>
 * For example, suppose the following properties are defined:
 * <pre>
 * {@code
 *  foo = 1
 *  foo.bar = 2
 *  foo.baz = 3
 * }
 * </pre> In such case, this class provides the following property values:
 * <pre>
 * {@code
 *  props.get("foo")     = 1
 *  props.get("foo.bar") = 2
 *  props.get("foo.zap") = 1 // Fallback to "foo"
 *  props.get("zap")     = null
 *  props.get("zap", 0)  = 0 // Fallback to the provided default value
 * }
 * </pre>
 *
 * @param <T> the type of values in this properties.
 */
public class HierarchicalProperties<T> {

    private final Map<String, T> props;


    public HierarchicalProperties() {
        this.props = new HashMap<>();
    }


    public HierarchicalProperties(Map<String, T> props) {
        this.props = new HashMap<>(props);
    }


    public Optional<T> get(String key) {
        while (true) {
            T value = this.props.get(key);
            if (value != null) {
                return Optional.of(value);
            }

            int separatorIndex = key.lastIndexOf('.');
            if (separatorIndex >= 0) {
                // Use the key prefix.
                key = key.substring(0, separatorIndex);
            } else {
                // There is no separator in the key, so nothing is found.
                return Optional.empty();
            }
        }
    }


    public T get(String key, T fallbackValue) {
        return this.get(key).orElse(fallbackValue);
    }
}

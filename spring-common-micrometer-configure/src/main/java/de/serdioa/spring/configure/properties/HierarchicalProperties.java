package de.serdioa.spring.configure.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


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
public class HierarchicalProperties<T> extends HashMap<String, T> {

    public HierarchicalProperties() {
        // Default super constructor initializes an empty map.
    }


    public HierarchicalProperties(Map<String, T> props) {
        super(props);
    }


    public HierarchicalProperties(Map<String, String> props, Function<String, T> transform) {
        for (Map.Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey();
            String strValue = entry.getValue();

            // Skip null values, since they are ignored by the getter (fallback to a paren property) anyway.
            if (strValue != null) {
                T value;
                try {
                    value = transform.apply(strValue);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Can not parse value '" + strValue + "'", ex);
                }
                super.put(key, value);
            }
        }
    }


    public Optional<T> getHierarchical(String key) {
        while (true) {
            T value = this.get(key);
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


    public T getHierarchical(String key, T fallbackValue) {
        return this.getHierarchical(key).orElse(fallbackValue);
    }
}

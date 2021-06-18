package de.serdioa.spring.properties;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CompositeProperties extends AbstractMap<String, String> {

    private final Set<Map.Entry<String, String>> entries;


    public CompositeProperties(Iterable<Map<String, String>> props) {
        Objects.requireNonNull(props, "props is null");

        Map<String, String> entries = new HashMap<>();

        for (Map<String, String> propsMap : props) {
            for (Map.Entry<String, String> mapEntry : propsMap.entrySet()) {
                String key = mapEntry.getKey();
                String value = mapEntry.getValue();

                entries.putIfAbsent(key, value);
            }
        }

        this.entries = Collections.unmodifiableSet(entries.entrySet());
    }


    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.entries;
    }
}

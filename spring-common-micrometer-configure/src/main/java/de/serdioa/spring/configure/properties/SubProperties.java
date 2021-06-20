package de.serdioa.spring.configure.properties;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;


/* package private */ class SubProperties extends AbstractMap<String, String> {

    private final Set<Map.Entry<String, String>> entries;


    public SubProperties(Properties props, String prefix) {
        Objects.requireNonNull(props, "props is null");
        Objects.requireNonNull(prefix, "prefix is null");

        String fullPrefix = prefix + '.';
        int fullPrefixLength = fullPrefix.length();

        Set<Map.Entry<String, String>> entrySet = new HashSet<>();

        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(fullPrefix)) {
                String subKey = key.substring(fullPrefixLength);
                String value = props.getProperty(key);

                Map.Entry<String, String> entry = new AbstractMap.SimpleImmutableEntry<>(subKey, value);
                entrySet.add(entry);
            }
        }

        this.entries = Collections.unmodifiableSet(entrySet);
    }


    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.entries;
    }
}

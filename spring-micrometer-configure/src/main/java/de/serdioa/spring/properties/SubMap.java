package de.serdioa.spring.properties;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/* package private */ class SubMap extends AbstractMap<String, String> {

    private final Set<Map.Entry<String, String>> entries;


    public SubMap(Map<String, String> map, String prefix) {
        Objects.requireNonNull(map, "map is null");
        Objects.requireNonNull(prefix, "prefix is null");

        String fullPrefix = prefix + '.';
        int fullPrefixLength = fullPrefix.length();

        Set<Map.Entry<String, String>> entrySet = new HashSet<>();

        for (Map.Entry<String, String> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            if (key.startsWith(fullPrefix)) {
                String subKey = key.substring(fullPrefixLength);
                String value = mapEntry.getValue();

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

package de.serdioa.spring.configure.metrics.filter;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;


/**
 * Micrometer filter for renaming meters.
 */
public class RenameFilter implements MeterFilter {

    private final Map<String, String> rename;


    public RenameFilter(Map<String, String> rename) {
        this.rename = Objects.requireNonNull(rename);
    }


    @Override
    public Meter.Id map(Meter.Id id) {
        // Check if we shall rename this meter.
        String pattern = rename.get(id.getName());
        if (pattern == null) {
            // No rename pattern for this meter is available, return the ID "as is".
            return id;
        }

        // Are there any placeholders for tags in the pattern?
        if (pattern.contains("${")) {
            // Does the meter ID contain any tags?
            Map<String, Object> tags = id.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue));
            if (!tags.isEmpty()) {
                // Use Spring utilities to replace "${...}" placeholders with tags.
                MapPropertySource tagsSource = new MapPropertySource("tags", tags);

                MutablePropertySources tagsSources = new MutablePropertySources();
                tagsSources.addFirst(tagsSource);

                PropertySourcesPropertyResolver tagsResolver = new PropertySourcesPropertyResolver(tagsSources);
                pattern = tagsResolver.resolvePlaceholders(pattern);
            }
        }

        // Rename the filter.
        Meter.Id newId = id.withName(pattern);

        return newId;
    }
}

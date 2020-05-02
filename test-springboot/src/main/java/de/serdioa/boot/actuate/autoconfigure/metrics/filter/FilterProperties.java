package de.serdioa.boot.actuate.autoconfigure.metrics.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;


public class FilterProperties {
    @Getter
    private final Map<String, Boolean> enabled = new LinkedHashMap<>();
}

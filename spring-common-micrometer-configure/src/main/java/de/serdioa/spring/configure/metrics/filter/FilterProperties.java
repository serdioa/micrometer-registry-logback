package de.serdioa.spring.configure.metrics.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;


public class FilterProperties {

    @Getter
    private final Map<String, Boolean> enabled = new LinkedHashMap<>();

    @Getter
    private final Map<String, String> rename = new LinkedHashMap<>();
}

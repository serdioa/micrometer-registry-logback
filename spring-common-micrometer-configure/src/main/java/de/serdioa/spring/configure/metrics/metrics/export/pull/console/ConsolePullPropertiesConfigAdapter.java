package de.serdioa.spring.configure.metrics.metrics.export.pull.console;

import java.time.Duration;

import de.serdioa.micrometer.pull.console.ConsolePullConfig;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.PropertiesConfigAdapter;


public class ConsolePullPropertiesConfigAdapter extends PropertiesConfigAdapter<ConsolePullProperties>
        implements ConsolePullConfig {

    public ConsolePullPropertiesConfigAdapter(ConsolePullProperties properties) {
        super(properties);
    }


    @Override
    public String get(String key) {
        return null;
    }


    @Override
    public boolean enabled() {
        return get(ConsolePullProperties::isEnabled, ConsolePullConfig.super::enabled);
    }


    @Override
    public Duration pollingFrequency() {
        return get(ConsolePullProperties::getPollingFrequency, ConsolePullConfig.super::pollingFrequency);
    }
}

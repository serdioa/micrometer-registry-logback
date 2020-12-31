package de.serdioa.micrometer.pull;

import io.micrometer.core.instrument.config.MeterRegistryConfig;

import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkAll;
import static io.micrometer.core.instrument.config.MeterRegistryConfigValidator.checkRequired;
import static io.micrometer.core.instrument.config.validate.PropertyValidator.*;

import java.time.Duration;

import io.micrometer.core.instrument.config.validate.Validated;


public interface PullConfig extends MeterRegistryConfig {

    default boolean enabled() {
        return true;
    }


    default Duration pollingFrequency() {
        return getDuration(this, "window").orElse(Duration.ofMinutes(1));
    }


    @Override
    default Validated<?> validate() {
        return checkAll(this,
                checkRequired("pollingFrequency", PullConfig::pollingFrequency)
        );
    }
}

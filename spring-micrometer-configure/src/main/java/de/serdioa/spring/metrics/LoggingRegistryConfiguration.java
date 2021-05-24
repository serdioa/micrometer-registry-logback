package de.serdioa.spring.metrics;

import java.time.Duration;

import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LoggingRegistryConfiguration {

    @Bean
    public LoggingRegistryProperties loggingRegistryProperties(StructuredPropertyService propertyService) {
        LoggingRegistryProperties props = new LoggingRegistryProperties();

        props.setEnabled(true);
        props.setStep(Duration.ofSeconds(10));

        return props;
    }


    @Bean
    public LoggingRegistryConfig loggingRegistryConfig(LoggingRegistryProperties props) {
        return new LoggingRegistryPropertiesConfigAdapter(props);
    }


    @Bean
    public MeterRegistry loggingMeterRegistry(LoggingRegistryConfig config, Clock clock) {
        return new LoggingMeterRegistry(config, clock);
    }

}

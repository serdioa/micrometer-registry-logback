package de.serdioa.spring.metrics;

import java.time.Duration;

import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingProperties;
import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingPropertiesConfigAdapter;
import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LoggingRegistryConfiguration {

    @Bean
    public LoggingProperties loggingRegistryProperties(StructuredPropertyService propertyService) {
        LoggingProperties props = new LoggingProperties();

        props.setEnabled(true);
        props.setStep(Duration.ofSeconds(10));

        return props;
    }


    @Bean
    public LoggingRegistryConfig loggingRegistryConfig(LoggingProperties props) {
        return new LoggingPropertiesConfigAdapter(props);
    }


    @Bean
    public MeterRegistry loggingMeterRegistry(LoggingRegistryConfig config, Clock clock) {
        return new LoggingMeterRegistry(config, clock);
    }

}

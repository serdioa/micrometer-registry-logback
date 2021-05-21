package de.serdioa.micrometer.spring.test;

import java.time.Duration;
import java.util.List;

import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;
import de.serdioa.spring.metrics.LoggingRegistryProperties;
import de.serdioa.spring.metrics.LoggingRegistryPropertiesConfigAdapter;
import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@PropertySource("classpath:/de/serdioa/micrometer/spring/test/application.properties")
public class ApplicationConfiguration {

    @Bean
    public TestBean testBean() {
        return new TestBean();
    }


    @Bean
    public StructuredPropertyService propertiesService() {
        return new StructuredPropertyService();
    }


    @Bean
    Clock micrometerClock() {
        return Clock.SYSTEM;
    }


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


    @Bean
    public MeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }


    @Bean
    @Primary
    public MeterRegistry compositeMeterRegistry(Clock clock, List<MeterRegistry> registries) {
        return new CompositeMeterRegistry(clock, registries);
    }
}

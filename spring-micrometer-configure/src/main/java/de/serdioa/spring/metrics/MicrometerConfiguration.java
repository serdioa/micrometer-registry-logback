package de.serdioa.spring.metrics;

import java.util.List;

import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class MicrometerConfiguration {

    @Bean
    public StructuredPropertyService propertiesService() {
        return new StructuredPropertyService();
    }


    @Bean
    Clock micrometerClock() {
        return Clock.SYSTEM;
    }


    @Bean
    @Primary
    public MeterRegistry compositeMeterRegistry(Clock clock, List<MeterRegistry> registries) {
        return new CompositeMeterRegistry(clock, registries);
    }
}

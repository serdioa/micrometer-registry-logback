package de.serdioa.micrometer.spring.test;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public MeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}

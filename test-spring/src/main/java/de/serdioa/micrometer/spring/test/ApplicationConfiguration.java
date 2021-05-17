package de.serdioa.micrometer.spring.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfiguration {
    @Bean
    public TestBean testBean() {
        return new TestBean();
    }
}

package de.serdioa.micrometer.spring.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:/de/serdioa/micrometer/spring/test/application.properties")
public class ApplicationConfiguration {
    @Bean
    public TestBean testBean() {
        return new TestBean();
    }
}

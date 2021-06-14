package de.serdioa.boot.actuate.autoconfigure.metrics.export.logging.agg;

import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingProperties;
import de.serdioa.spring.configure.metrics.export.logging.agg.LoggingPropertiesConfigAdapter;
import de.serdioa.spring.configure.metrics.filter.FilterMeterRegistryCustomizer;
import de.serdioa.micrometer.logging.agg.LoggingMeterRegistry;
import de.serdioa.micrometer.logging.agg.LoggingRegistryConfig;

import io.micrometer.core.instrument.Clock;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({CompositeMeterRegistryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(Clock.class)
@ConditionalOnClass(LoggingMeterRegistry.class)
@ConditionalOnProperty(prefix = "management.metrics.export.logging.agg", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingMetricsExportAutoConfiguration {

    private final LoggingProperties properties;


    public LoggingMetricsExportAutoConfiguration(LoggingProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean
    public LoggingRegistryConfig loggingConfig() {
        return new LoggingPropertiesConfigAdapter(this.properties);
    }


    @Bean
    @ConditionalOnMissingBean
    public LoggingMeterRegistry loggingMeterRegistry(LoggingRegistryConfig registryConfig, Clock clock) {
        return new LoggingMeterRegistry(registryConfig, clock);
    }


    @Bean
    public MeterRegistryCustomizer<LoggingMeterRegistry> loggingFilterMeterRegistryCustomizer() {
        return new FilterMeterRegistryCustomizer<>(this.properties.getFilter());
    }
}

package de.serdioa.boot.actuate.autoconfigure.metrics.export.logging.direct;

import de.serdioa.micrometer.logging.direct.DirectLoggingMeterRegistry;
import de.serdioa.micrometer.logging.direct.DirectLoggingRegistryConfig;

import io.micrometer.core.instrument.Clock;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
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
@ConditionalOnClass(DirectLoggingMeterRegistry.class)
@ConditionalOnProperty(prefix = "management.metrics.export.logging.direct", name = "enabled", havingValue = "true",
        matchIfMissing = false)
@EnableConfigurationProperties(DirectLoggingProperties.class)
public class DirectLoggingMetricsExportAutoConfiguration {

    private final DirectLoggingProperties properties;


    public DirectLoggingMetricsExportAutoConfiguration(DirectLoggingProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean
    public DirectLoggingRegistryConfig directLoggingConfig() {
        return new DirectLoggingPropertiesConfigAdapter(this.properties);
    }


    @Bean
    @ConditionalOnMissingBean
    public DirectLoggingMeterRegistry directLoggingMeterRegistry(DirectLoggingRegistryConfig registryConfig, Clock clock) {
        return new DirectLoggingMeterRegistry(registryConfig, clock);
    }
}

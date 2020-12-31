package de.serdioa.boot.actuate.autoconfigure.metrics.export.pull.console;

import de.serdioa.boot.actuate.autoconfigure.metrics.filter.FilterMeterRegistryCustomizer;
import de.serdioa.micrometer.pull.console.ConsolePullConfig;
import de.serdioa.micrometer.pull.console.ConsolePullMeterRegistry;

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
@ConditionalOnClass(ConsolePullMeterRegistry.class)
@ConditionalOnProperty(prefix = "management.metrics.export.pull.console", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(ConsolePullProperties.class)
public class ConsolePullMetricsExportAutoConfiguration {

    private final ConsolePullProperties properties;


    public ConsolePullMetricsExportAutoConfiguration(ConsolePullProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean
    public ConsolePullConfig consolePullConfig() {
        return new ConsolePullPropertiesConfigAdapter(this.properties);
    }


    @Bean
    @ConditionalOnMissingBean
    public ConsolePullMeterRegistry consolePullMeterRegistry(ConsolePullConfig registryConfig, Clock clock) {
        return new ConsolePullMeterRegistry(registryConfig, clock);
    }


    @Bean
    public MeterRegistryCustomizer<ConsolePullMeterRegistry> consolePullFilterMeterRegistryCustomizer() {
        return new FilterMeterRegistryCustomizer<>(this.properties.getFilter());
    }
}

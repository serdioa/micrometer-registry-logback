package de.serdioa.spring.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * Post-processor for {@link MeterRegistry} beans to apply configuration.
 */
public class MeterRegistryPostProcessor implements BeanPostProcessor {

    private final ObjectProvider<MeterFilter> filters;


    public MeterRegistryPostProcessor(ObjectProvider<MeterFilter> filters) {
        this.filters = filters;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MeterRegistry) {
            this.postProcessMeterRegistry((MeterRegistry) bean);
        }
        return bean;
    }


    private void postProcessMeterRegistry(MeterRegistry meterRegistry) {
        this.addFilters(meterRegistry);
    }


    private void addFilters(MeterRegistry meterRegistry) {
        this.filters.orderedStream().forEach(meterRegistry.config()::meterFilter);
    }
}

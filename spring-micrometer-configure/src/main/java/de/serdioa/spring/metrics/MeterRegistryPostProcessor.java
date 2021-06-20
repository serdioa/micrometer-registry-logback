package de.serdioa.spring.metrics;

import java.util.List;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.util.LambdaSafe;


/**
 * Post-processor for {@link MeterRegistry} beans to apply configuration.
 */
public class MeterRegistryPostProcessor implements BeanPostProcessor {

    private final ObjectProvider<MeterRegistryCustomizer<?>> customizers;

    private final ObjectProvider<MeterFilter> filters;

    private final ObjectProvider<MeterBinder> binders;


    public MeterRegistryPostProcessor(ObjectProvider<MeterRegistryCustomizer<?>> customizers,
            ObjectProvider<MeterFilter> filters, ObjectProvider<MeterBinder> binders) {
        this.customizers = customizers;
        this.filters = filters;
        this.binders = binders;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MeterRegistry) {
            this.postProcessMeterRegistry((MeterRegistry) bean);
        }
        return bean;
    }


    private void postProcessMeterRegistry(MeterRegistry meterRegistry) {
        // Apply customization before registering filters and binders. Customization may change defaults (for example,
        // for percentiles).
        this.customize(meterRegistry);

        this.addFilters(meterRegistry);
        this.addBinders(meterRegistry);
    }


    @SuppressWarnings("unchecked")
    private void customize(MeterRegistry registry) {
        List<MeterRegistryCustomizer<?>> customizersList =
                this.customizers.orderedStream().collect(Collectors.toList());

        LambdaSafe.callbacks(MeterRegistryCustomizer.class, customizersList, registry)
                .withLogger(MeterRegistryPostProcessor.class).invoke((customizer) -> customizer.customize(registry));
    }


    private void addFilters(MeterRegistry meterRegistry) {
        this.filters.orderedStream().forEach(meterRegistry.config()::meterFilter);
    }


    private void addBinders(MeterRegistry meterRegistry) {
        this.binders.orderedStream().forEach(binder -> binder.bindTo(meterRegistry));
    }
}

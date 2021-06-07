package de.serdioa.spring.properties;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class StructuredPropertyService implements ApplicationContextAware {

    private ApplicationContext ctx;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = Objects.requireNonNull(applicationContext);
    }


    public Map<String, String> getProperties(String prefix) {
        return PropertiesUtil.sub(this.ctx, prefix);
    }


    public <T> HierarchicalProperties<T> getHierarchicalProperties(String prefix, Function<String, T> parser) {
        Map<String, String> props = this.getProperties(prefix);
        return new HierarchicalProperties<>(props, parser);
    }
}

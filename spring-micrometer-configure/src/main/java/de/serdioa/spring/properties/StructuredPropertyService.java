package de.serdioa.spring.properties;

import java.util.Map;
import java.util.Objects;

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
}

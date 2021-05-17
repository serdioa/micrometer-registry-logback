package de.serdioa.micrometer.spring.test;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


public class Application {
    public static void main(String [] args) {
        // Create a stand-alone Spring application context.
        GenericApplicationContext ctx = new GenericApplicationContext();
        
        // Register a shutdown hook to properly destroy the Spring application context when the application
        // is shut down.
        ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) ctx;
        configCtx.registerShutdownHook();
        
        // Load definitions of Spring beans.
        new XmlBeanDefinitionReader(ctx).loadBeanDefinitions("/de/serdioa/micrometer/spring/test/services.xml");
        ctx.refresh();
        
        // ... wait until JVM exits, calling the registered shutdown hook.
    }
}

package de.serdioa.micrometer.spring.test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


public class TestBean {
    @PostConstruct
    public void initialize() {
        System.out.println("TestBean.initialize()");
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("TestBean.destroy()");
    }
}

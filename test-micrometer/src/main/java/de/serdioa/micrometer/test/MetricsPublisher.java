package de.serdioa.micrometer.test;


public interface MetricsPublisher {

    void start();


    void stop() throws InterruptedException;
}

package de.serdioa.mtest;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;


public class Main {
    private final MetricsPublisher publisher;
    private final MetricsConsumer consumer;
    
    private final Thread publisherThread;
    private final Thread consumerThread;
    
    private final MeterRegistry meterRegistry;
    
    public static void main(String [] args) throws Exception {
        final Main main = new Main();
        main.start();
        
        Thread.sleep(30000);
        
        main.stop();
    }
    
    private Main() {
        this.meterRegistry = new SimpleMeterRegistry();
        
        this.publisher = new MetricsPublisher(this.meterRegistry);
        this.consumer = new MetricsConsumer(this.meterRegistry);
        
        this.publisherThread = new Thread(this.publisher);
        this.consumerThread = new Thread(this.consumer);
    }
    
    
    private void start() {
        System.out.println("Starting threads...");
        
        this.publisherThread.start();
        this.consumerThread.start();
        
        System.out.println("Threads has been started");
    }
    
    
    private void stop() throws InterruptedException {
        System.out.println("Stopping threads...");
        
        this.publisherThread.interrupt();
        this.consumerThread.interrupt();
        
        this.publisherThread.join();
        this.consumerThread.join();
        
        System.out.println("Threads has been stopped");
        
        this.meterRegistry.close();
        System.out.println("Meter registry has been closed");
    }
}

package de.serdioa.mtest;


public class Main {
    private final MetricsPublisher publisher;
    private final MetricsConsumer consumer;
    
    private final Thread publisherThread;
    private final Thread consumerThread;
    
    public static void main(String [] args) throws Exception {
        final Main main = new Main();
        main.start();
        
        Thread.sleep(5000);
        
        main.stop();
    }
    
    private Main() {
        this.publisher = new MetricsPublisher();
        this.consumer = new MetricsConsumer();
        
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
    }
}

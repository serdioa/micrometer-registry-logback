package de.serdioa.mtest;

import java.util.Random;


public class MetricsPublisher implements Runnable {
    private final Random rnd = new Random();
    
    @Override
    public void run() {
        try {
            while (true) {
                publish();
            }          
        } catch (InterruptedException ex) {
            System.out.println("Publisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }
    
    
    private void publish() throws InterruptedException {
        final long startTs = System.nanoTime();
        doWork();
        final long endTs = System.nanoTime();
        final long duration = endTs - startTs;
        
        System.out.println("Publisher: " + duration);
    }
    
    
    private void doWork() throws InterruptedException {
        final double gauss = this.rnd.nextGaussian();
        long sleep = (long) (gauss * 10 + 500);
        sleep = (sleep > 0 ? sleep : 0);
        
        Thread.sleep(sleep);
    }
}

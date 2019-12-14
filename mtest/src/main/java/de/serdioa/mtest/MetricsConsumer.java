package de.serdioa.mtest;


public class MetricsConsumer implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                consume();
            }          
        } catch (InterruptedException ex) {
            System.out.println("Consumer has been interrupted");
            Thread.currentThread().interrupt();
        }
    }
    
    private void consume() throws InterruptedException {
        System.out.println("Consumer::consume");
        Thread.sleep(1000);
    }
}

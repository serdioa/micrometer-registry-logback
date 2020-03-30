package de.serdioa.micrometer.test;

import java.util.Objects;

import io.micrometer.core.instrument.MeterRegistry;


public abstract class AbstractMetricsPublisher implements MetricsPublisher {

    protected final MeterRegistry registry;

    private final Thread thread = new Thread(this::run, this.getClass().getSimpleName() + "-worker");


    public AbstractMetricsPublisher(MeterRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }


    public void start() {
        this.thread.start();
    }


    public void stop() throws InterruptedException {
        this.thread.interrupt();
        this.thread.join();
    }


    private void run() {
        System.out.println("Starting " + this.getClass().getSimpleName());

        try {
            while (!Thread.interrupted()) {
                this.publish();
            }
        } catch (InterruptedException ex) {
            // Exit the loop.
        }

        System.out.println("Stopped " + this.getClass().getSimpleName());
    }


    protected abstract void publish() throws InterruptedException;
}

package de.serdioa.micrometer.test;

import java.util.Objects;

import io.micrometer.core.instrument.MeterRegistry;


public class MetricsConsumer {

    private final MeterRegistry meterRegistry;
    private Thread thread;
    private final Object mutex = new Object();


    public MetricsConsumer(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);
    }


    public void start() {
        synchronized (this.mutex) {
            if (this.thread != null) {
                throw new IllegalStateException("already running");
            }

            this.thread = new Thread(this::run);
            this.thread.start();
        }
    }


    public void stop() throws InterruptedException {
        final Thread worker;

        synchronized (this.mutex) {
            if (this.thread != null) {
                worker = this.thread;
                this.thread = null;
            } else {
                worker = null;
            }
        }

        if (worker != null) {
            worker.interrupt();
            worker.join();
        }
    }


    private void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println(MetricsFormatter.format(this.meterRegistry));

                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            System.out.println("Consumer has been interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

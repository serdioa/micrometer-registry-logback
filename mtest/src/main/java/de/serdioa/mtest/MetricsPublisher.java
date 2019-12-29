package de.serdioa.mtest;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import io.micrometer.core.instrument.MeterRegistry;


public class MetricsPublisher implements Runnable {
    private final MeterRegistry meterRegistry;

    private final List<Thread> workers = new CopyOnWriteArrayList<>();


    public MetricsPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        addPublisher(new TimerPublisher(this.meterRegistry), "timer-publisher");
    }


    private void addPublisher(Runnable publisher, String name) {
        Thread t = new Thread(publisher, name);
        this.workers.add(t);
    }


    @Override
    public void run() {
        try {
            startWorkers();
            joinWorkers();
        } catch (InterruptedException ex) {
            System.out.println("Publisher has been interrupted");
            stopWorkers();
            Thread.currentThread().interrupt();
        }
    }


    private void startWorkers() {
        for (Thread t : this.workers) {
            t.start();
        }
    }


    private void joinWorkers() throws InterruptedException {
        for (Thread t : this.workers) {
            t.join();
        }
    }


    private void stopWorkers() {
        for (Thread t : this.workers) {
            t.interrupt();
        }
    }
}

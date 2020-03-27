package de.serdioa.micrometer.test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import io.micrometer.core.instrument.MeterRegistry;


public class MetricsPublisher implements Runnable {
    private final MeterRegistry meterRegistry;

    private final List<Thread> workers = new CopyOnWriteArrayList<>();


    public MetricsPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        // addPublisher(new JvmMetricsPublisher(this.meterRegistry), "jvm-publisher");
        addPublisher(new TimerPublisher(this.meterRegistry), "timer-publisher");
        // addPublisher(new GaugePublisher(this.meterRegistry), "gauge-publisher");
        // addPublisher(new TimeGaugePublisher(this.meterRegistry), "time-gauge-publisher");
        // addPublisher(new CounterPublisher(this.meterRegistry), "counter-publisher");
        // addPublisher(new DistributionSummaryPublisher(this.meterRegistry), "ds-publisher");
        // addPublisher(new LongTaskTimerPublisher(this.meterRegistry), "ltt-publisher");
        // addPublisher(new FunctionCounterPublisher(this.meterRegistry), "function-counter-publisher");
        // addPublisher(new FunctionTimerPublisher(this.meterRegistry), "function-timer-publisher");
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
        this.workers.forEach(Thread::start);
    }


    private void joinWorkers() throws InterruptedException {
        for (Thread t : this.workers) {
            t.join();
        }
    }


    private void stopWorkers() {
        this.workers.forEach(Thread::interrupt);
    }
}

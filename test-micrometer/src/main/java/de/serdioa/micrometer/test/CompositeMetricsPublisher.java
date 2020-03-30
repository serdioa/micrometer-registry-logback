package de.serdioa.micrometer.test;

import java.util.ArrayList;
import java.util.List;


public class CompositeMetricsPublisher implements MetricsPublisher {

    private final List<MetricsPublisher> publishers = new ArrayList<>();

    private boolean running = false;

    private final Object mutex = new Object();


    public void add(MetricsPublisher publisher) {
        synchronized (this.mutex) {
            if (this.running) {
                throw new IllegalStateException("running");
            }

            this.publishers.add(publisher);
        }
    }


    @Override
    public void start() {
        System.out.println("Starting " + this.getClass().getSimpleName());

        synchronized (this.mutex) {
            if (this.running) {
                throw new IllegalStateException("running");
            }

            for (MetricsPublisher publisher : this.publishers) {
                publisher.start();
            }
            this.running = true;
        }

        System.out.println("Started " + this.getClass().getSimpleName());
    }


    @Override
    public void stop() throws InterruptedException {
        System.out.println("Stopping " + this.getClass().getSimpleName());

        synchronized (this.mutex) {
            if (!this.running) {
                return;
            }

            for (MetricsPublisher publisher : this.publishers) {
                publisher.stop();
            }
            this.running = false;
        }

        System.out.println("Stopped " + this.getClass().getSimpleName());
    }
}

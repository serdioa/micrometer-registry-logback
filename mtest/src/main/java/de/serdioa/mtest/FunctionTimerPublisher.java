package de.serdioa.mtest;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.MeterRegistry;


public class FunctionTimerPublisher implements Runnable {

    private final MeterRegistry meterRegistry;
    private final State state = new State();
    private final Random rnd = new Random();


    public FunctionTimerPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry);

        FunctionTimer.builder("publisher.functionTimer", this.state, State::getCount, State::getTotalTime, TimeUnit.SECONDS)
                .tags("g.\n1", "v.\n1", "g.2", "v.2")
                .register(this.meterRegistry);
    }


    @Override
    public void run() {
        System.out.println("FunctionTimerPublisher has been started");
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException ex) {
            System.out.println("FunctionTimerPublisher has been interrupted");
            Thread.currentThread().interrupt();
        }
    }


    private void publish() throws InterruptedException {
        double increment = Math.abs(10.0 + this.rnd.nextGaussian());
        this.state.add(increment);

        long sleep = (long) (this.rnd.nextInt(1000));
        Thread.sleep(sleep);
    }


    private static class State {

        private int count;
        private double totalTime;


        public synchronized void add(double increment) {
            this.count++;
            this.totalTime += increment;
        }


        public synchronized int getCount() {
            return this.count;
        }


        public synchronized double getTotalTime() {
            return this.totalTime;
        }
    }
}

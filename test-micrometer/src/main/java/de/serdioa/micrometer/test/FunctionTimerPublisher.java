package de.serdioa.micrometer.test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.MeterRegistry;


public class FunctionTimerPublisher extends AbstractMetricsPublisher {

    private final State state = new State();
    private final Random rnd = new Random();


    public FunctionTimerPublisher(MeterRegistry registry) {
        super(registry);

        FunctionTimer.builder("publisher.functionTimer", this.state, State::getCount, State::getTotalTime, TimeUnit.SECONDS)
                .tags("g.\n1", "v.\n1", "g.2", "v.2")
                .register(this.registry);

    }


    @Override
    protected void publish() throws InterruptedException {
        double increment = Math.abs(10.0 + this.rnd.nextGaussian());
        this.state.add(increment);

        long sleep = (long) (this.rnd.nextInt(100));
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

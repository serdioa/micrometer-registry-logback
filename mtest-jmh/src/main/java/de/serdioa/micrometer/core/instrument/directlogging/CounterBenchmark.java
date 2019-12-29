package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 10, time = 10)
@Measurement(iterations = 10, time = 10)
public class CounterBenchmark {

    private static final int VALUES_COUNT = 100000;
    private static final Random rnd = new Random(123);

    @Param({"simple", "logging"})
//    @Param({"logging"})
    private String metricsMode;

//    @Param({"noop", "async"})
    @Param({"noop", "sync", "async"})
//    @Param({"noop"})
//    @Param({"console"})
    private String loggingMode;

    private MeterRegistry meterRegistry;
    private Counter counter;
    private Counter counterWithTags;

    private double[] values;
    private int index;


    @Setup
    public void setup() {
        switch (this.metricsMode) {
            case "simple":
                this.meterRegistry = buildSimpleMeterRegistry();
                break;
            case "logging":
                this.meterRegistry = buildLoggingMeterRegistry();
                break;
            default:
                throw new IllegalStateException("Unexpected metricsMode:" + this.metricsMode);
        }
        this.counter = Counter.builder("c1").register(this.meterRegistry);
        this.counterWithTags = Counter.builder("c2")
                .tags("k1", "v1", "k2", "v2")
                .register(this.meterRegistry);

        this.values = new double[VALUES_COUNT];
        for (int i = 0; i < VALUES_COUNT; ++i) {
            this.values[i] = rnd.nextDouble();
        }
        this.index = 0;

        LogbackConfigurator logbackConfig = new LogbackConfigurator().json(true);
        switch (this.loggingMode) {
            case "noop":
                logbackConfig.type(LogbackConfigurator.Type.NOOP);
                break;
            case "sync":
                logbackConfig.type(LogbackConfigurator.Type.FILE)
                        .fileName("logback-sync." + this.metricsMode + ".log")
                        .asynchronous(false);
                break;
            case "async":
                logbackConfig.type(LogbackConfigurator.Type.FILE)
                        .fileName("logback-async." + this.metricsMode + ".log")
                        .asynchronous(true);
                break;
            case "console":
                logbackConfig.type(LogbackConfigurator.Type.CONSOLE)
                        .asynchronous(true);
            default:
            // Skip
        }
        logbackConfig.configure();
    }


    @TearDown
    public void tearDown() {
        LogbackConfigurator.stop();
    }


    private MeterRegistry buildSimpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }


    private MeterRegistry buildLoggingMeterRegistry() {
        return new DirectLoggingMeterRegistry();
    }


    @Benchmark
    public void increment() {
        this.counter.increment();
    }


    @Benchmark
    public void incrementByValue() {
        this.counter.increment(this.values[this.index]);
        if ((++this.index) >= VALUES_COUNT) {
            this.index = 0;
        }
    }


    @Benchmark
    public void incrementWithTags() {
        this.counterWithTags.increment();
    }


    @Benchmark
    public void incrementByValueWithTags() {
        this.counterWithTags.increment(this.values[this.index]);
        if ((++this.index) >= VALUES_COUNT) {
            this.index = 0;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CounterBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

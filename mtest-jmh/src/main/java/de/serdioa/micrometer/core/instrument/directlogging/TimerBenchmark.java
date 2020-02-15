package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
public class TimerBenchmark {

    private static final int TS_COUNT = 100000;
    private static final Random rnd = new Random(123);

//    @Param({"simple", "logging"})
    @Param({"logging"})
    private String metricsMode;

    @Param({"noop", "async"})
//    @Param({"noop", "sync", "async"})
//    @Param({"noop"})
//    @Param({"console"})
    private String loggingMode;

    private MeterRegistry meterRegistry;
    private Timer timer;
    private Timer timerWithTags;

    private long [] ts;
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
        this.timer = this.meterRegistry.timer("t1");
        this.timerWithTags = this.meterRegistry.timer("t2", "k1", "v1", "k2", "v2");

        this.ts = new long[TS_COUNT];
        for (int i = 0; i < TS_COUNT; ++i) {
            this.ts[i] = rnd.nextInt(10000);
        }
        this.index = 0;

        LogbackConfigurator logbackConfig = new LogbackConfigurator().encoderType(
                LogbackConfigurator.EncoderType.JSONLOGSTASH);
        switch (this.loggingMode) {
            case "noop":
                logbackConfig.destination(LogbackConfigurator.Destination.NOOP);
                break;
            case "sync":
                logbackConfig.destination(LogbackConfigurator.Destination.FILE)
                        .fileName("logback-sync." + this.metricsMode + ".log")
                        .asynchronous(false);
                break;
            case "async":
                logbackConfig.destination(LogbackConfigurator.Destination.FILE)
                        .fileName("logback-async." + this.metricsMode + ".log")
                        .asynchronous(true);
                break;
            case "console":
                logbackConfig.destination(LogbackConfigurator.Destination.CONSOLE)
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
    public void timer() {
        this.timer.record(this.ts[index], TimeUnit.MILLISECONDS);
        if ((++this.index) >= TS_COUNT) {
            this.index = 0;
        }
    }


    @Benchmark
    public void timerWithTags() {
        this.timerWithTags.record(this.ts[index], TimeUnit.MILLISECONDS);
        if ((++this.index) >= TS_COUNT) {
            this.index = 0;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TimerBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

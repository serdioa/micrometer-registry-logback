package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.LongTaskTimer;
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
public class LongTaskTimerBenchmark {

//    @Param({"simple", "logging"})
    @Param({"logging"})
    private String metricsMode;

    @Param({"noop", "async"})
//    @Param({"noop", "sync", "async"})
//    @Param({"noop"})
//    @Param({"console"})
    private String loggingMode;

    private MeterRegistry meterRegistry;
    private LongTaskTimer timer;
    private LongTaskTimer timerWithTags;

    private LongTaskTimer.Sample task;


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
        this.timer = this.meterRegistry.more().longTaskTimer("t1");
        this.timerWithTags = this.meterRegistry.more().longTaskTimer("t2", "k1", "v1", "k2", "v2");

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
    public void timer() {
        if (this.task == null) {
            this.task = this.timer.start();
        } else {
            this.task.stop();
            this.task = null;
        }
    }


    @Benchmark
    public void timerWithTags() {
        if (this.task == null) {
            this.task = this.timerWithTags.start();
        } else {
            this.task.stop();
            this.task = null;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LongTaskTimerBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

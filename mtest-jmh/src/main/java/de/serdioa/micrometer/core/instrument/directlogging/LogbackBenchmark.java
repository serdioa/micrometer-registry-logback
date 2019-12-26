package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.concurrent.TimeUnit;

import net.logstash.logback.argument.StructuredArguments;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 5, time = 5)
public class LogbackBenchmark {

    @Param({"NOOP", "CONSOLE", "FILE"})
    private LogbackConfigurator.Type logbackType;

    @Param({"true", "false"})
    private boolean asynchronous;

    @Param({"true", "false"})
    private boolean json;

    private final Logger logger = LoggerFactory.getLogger("test");


    @Setup
    public void setup() {
        StringBuilder sb = new StringBuilder("logback");
        if (this.asynchronous) {
            sb.append(".async");
        }
        if (this.json) {
            sb.append(".json");
        }
        sb.append(".log");

        new LogbackConfigurator()
                .type(this.logbackType)
                .asynchronous(this.asynchronous)
                .json(this.json)
                .fileName(sb.toString())
                .configure();
    }


    @TearDown
    public void tearDown() {
        LogbackConfigurator.stop();
    }


    @Benchmark
    public void logDisabledLevel() {
        logger.trace("test TRACE");
    }


    @Benchmark
    public void logEnabledLevel() {
        logger.info("test INFO");
    }


    @Benchmark
    public void logDisabledLevelJson() {
        logger.trace("test JSON TRACE", StructuredArguments.keyValue("age", 20));
    }


    @Benchmark
    public void logEnabledLevelJson() {
        logger.info("test JSON INFO", StructuredArguments.keyValue("age", 20));
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LogbackBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

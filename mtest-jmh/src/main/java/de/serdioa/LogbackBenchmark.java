package de.serdioa;

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


/*
Benchmark                              (asynchronous)  (json)  (logbackType)  Mode  Cnt      Score      Error  Units
LogbackBenchmark.logDisabledLevel                true    true           NOOP  avgt    5      1.136 ±    0.001  ns/op
LogbackBenchmark.logDisabledLevel                true    true        CONSOLE  avgt    5      1.129 ±    0.008  ns/op
LogbackBenchmark.logDisabledLevel                true    true           FILE  avgt    5      1.127 ±    0.007  ns/op
LogbackBenchmark.logDisabledLevel                true   false           NOOP  avgt    5      1.136 ±    0.061  ns/op
LogbackBenchmark.logDisabledLevel                true   false        CONSOLE  avgt    5      1.140 ±    0.005  ns/op
LogbackBenchmark.logDisabledLevel                true   false           FILE  avgt    5      1.130 ±    0.002  ns/op
LogbackBenchmark.logDisabledLevel               false    true           NOOP  avgt    5      1.137 ±    0.008  ns/op
LogbackBenchmark.logDisabledLevel               false    true        CONSOLE  avgt    5      1.142 ±    0.007  ns/op
LogbackBenchmark.logDisabledLevel               false    true           FILE  avgt    5      1.140 ±    0.007  ns/op
LogbackBenchmark.logDisabledLevel               false   false           NOOP  avgt    5      1.139 ±    0.002  ns/op
LogbackBenchmark.logDisabledLevel               false   false        CONSOLE  avgt    5      1.137 ±    0.010  ns/op
LogbackBenchmark.logDisabledLevel               false   false           FILE  avgt    5      1.135 ±    0.012  ns/op

LogbackBenchmark.logDisabledLevelJson            true    true           NOOP  avgt    5      1.313 ±    0.031  ns/op
LogbackBenchmark.logDisabledLevelJson            true    true        CONSOLE  avgt    5      1.314 ±    0.037  ns/op
LogbackBenchmark.logDisabledLevelJson            true    true           FILE  avgt    5      1.310 ±    0.010  ns/op
LogbackBenchmark.logDisabledLevelJson            true   false           NOOP  avgt    5      1.306 ±    0.002  ns/op
LogbackBenchmark.logDisabledLevelJson            true   false        CONSOLE  avgt    5      1.306 ±    0.003  ns/op
LogbackBenchmark.logDisabledLevelJson            true   false           FILE  avgt    5      1.307 ±    0.010  ns/op
LogbackBenchmark.logDisabledLevelJson           false    true           NOOP  avgt    5      1.312 ±    0.013  ns/op
LogbackBenchmark.logDisabledLevelJson           false    true        CONSOLE  avgt    5      1.307 ±    0.008  ns/op
LogbackBenchmark.logDisabledLevelJson           false    true           FILE  avgt    5      1.307 ±    0.008  ns/op
LogbackBenchmark.logDisabledLevelJson           false   false           NOOP  avgt    5      1.307 ±    0.008  ns/op
LogbackBenchmark.logDisabledLevelJson           false   false        CONSOLE  avgt    5      1.306 ±    0.003  ns/op
LogbackBenchmark.logDisabledLevelJson           false   false           FILE  avgt    5      1.306 ±    0.003  ns/op

LogbackBenchmark.logEnabledLevel                 true    true           NOOP  avgt    5    277.406 ±   12.835  ns/op
LogbackBenchmark.logEnabledLevel                 true    true        CONSOLE  avgt    5    506.339 ±   71.199  ns/op
LogbackBenchmark.logEnabledLevel                 true    true           FILE  avgt    5     89.053 ±   10.926  ns/op
LogbackBenchmark.logEnabledLevel                 true   false           NOOP  avgt    5    270.919 ±   11.249  ns/op
LogbackBenchmark.logEnabledLevel                 true   false        CONSOLE  avgt    5    760.247 ±  171.621  ns/op
LogbackBenchmark.logEnabledLevel                 true   false           FILE  avgt    5     93.000 ±   11.970  ns/op
LogbackBenchmark.logEnabledLevel                false    true           NOOP  avgt    5     34.777 ±    0.165  ns/op
LogbackBenchmark.logEnabledLevel                false    true        CONSOLE  avgt    5  13395.853 ± 1145.504  ns/op
LogbackBenchmark.logEnabledLevel                false    true           FILE  avgt    5   3874.910 ± 1978.383  ns/op
LogbackBenchmark.logEnabledLevel                false   false           NOOP  avgt    5     34.906 ±    0.175  ns/op
LogbackBenchmark.logEnabledLevel                false   false        CONSOLE  avgt    5   9251.232 ±  959.702  ns/op
LogbackBenchmark.logEnabledLevel                false   false           FILE  avgt    5   2140.938 ±  906.668  ns/op

LogbackBenchmark.logEnabledLevelJson             true    true           NOOP  avgt    5    374.161 ±   15.466  ns/op
LogbackBenchmark.logEnabledLevelJson             true    true        CONSOLE  avgt    5    721.163 ±  113.173  ns/op
LogbackBenchmark.logEnabledLevelJson             true    true           FILE  avgt    5    126.815 ±    5.748  ns/op
LogbackBenchmark.logEnabledLevelJson             true   false           NOOP  avgt    5    346.957 ±   16.157  ns/op
LogbackBenchmark.logEnabledLevelJson             true   false        CONSOLE  avgt    5    920.880 ±  112.080  ns/op
LogbackBenchmark.logEnabledLevelJson             true   false           FILE  avgt    5    124.720 ±    6.332  ns/op
LogbackBenchmark.logEnabledLevelJson            false    true           NOOP  avgt    5     39.815 ±    0.576  ns/op
LogbackBenchmark.logEnabledLevelJson            false    true        CONSOLE  avgt    5  14669.563 ± 2519.249  ns/op
LogbackBenchmark.logEnabledLevelJson            false    true           FILE  avgt    5   3942.885 ±   73.750  ns/op
LogbackBenchmark.logEnabledLevelJson            false   false           NOOP  avgt    5     38.586 ±    0.359  ns/op
LogbackBenchmark.logEnabledLevelJson            false   false        CONSOLE  avgt    5   9492.717 ±  500.675  ns/op
LogbackBenchmark.logEnabledLevelJson            false   false           FILE  avgt    5   2047.371 ±   33.847  ns/op
 */

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

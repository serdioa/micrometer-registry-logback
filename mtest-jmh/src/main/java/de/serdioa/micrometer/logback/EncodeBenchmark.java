package de.serdioa.micrometer.logback;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import com.fasterxml.jackson.core.JsonGenerator;
import de.serdioa.micrometer.core.instrument.directlogging.LogbackConfigurator;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.Markers;
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
import org.slf4j.Marker;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 10, time = 10)
@Measurement(iterations = 10, time = 10)
public class EncodeBenchmark {

    private static final int VALUES_COUNT = 100000;
    private static final Random rnd = new Random(123);

    @Param({"1", "2", "3", "5", "10"})
    private int paramCount;

    @Param({"0", "1", "2", "3", "5"})
    private int markersCount;

    private LogbackConfigurator conf;

    private Encoder<ILoggingEvent> encoder;

    private Logger logger;

    private ILoggingEvent[] events;
    private int index;


    @Setup
    public void setup() {
        this.conf = new LogbackConfigurator()
                .destination(LogbackConfigurator.Destination.NOOP)
                .encoderType(LogbackConfigurator.EncoderType.JSONLOGSTASH)
                .configure();

        this.encoder = this.conf.encoder();
        this.logger = this.conf.logCtx().getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        this.events = new ILoggingEvent[VALUES_COUNT];
        for (int i = 0; i < VALUES_COUNT; ++i) {
            this.events[i] = buildLoggingEvent(this.paramCount, this.markersCount);
        }
    }


    @TearDown
    public void tearDown() {
        LogbackConfigurator.stop();
    }


    private ILoggingEvent buildLoggingEvent(int paramCount, int markersCount) {
        Map<String, Integer> param = buildLoggingEventParameters(paramCount);
        StructuredArgument arg = new TestStructuredArgument(param);
        LoggingEvent event = new LoggingEvent(Logger.FQCN, logger, Level.INFO, null, null, new Object[]{arg});

        if (markersCount > 0) {
            Map<String, String> markers = buildLoggingEventMarkers(markersCount);
            Marker marker = Markers.appendEntries(markers);
            event.setMarker(marker);
        }

        return event;
    }


    private Map<String, Integer> buildLoggingEventParameters(int paramCount) {
        Map<String, Integer> param = new LinkedHashMap<>();
        for (int i = 0; i < paramCount; ++i) {
            param.put("p" + i, rnd.nextInt());
        }

        return param;
    }


    private Map<String, String> buildLoggingEventMarkers(int markersCount) {
        Map<String, String> markers = new LinkedHashMap<>();
        for (int i = 0; i < markersCount; ++i) {
            markers.put("k" + i, "v" + rnd.nextInt());
        }

        return markers;
    }


    @Benchmark
    public byte[] encode() {
        if ((++this.index) >= VALUES_COUNT) {
            this.index = 0;
        }

        return this.encoder.encode(this.events[this.index]);
    }


    private static class TestStructuredArgument implements StructuredArgument {

        private final Map<String, Integer> param;


        public TestStructuredArgument(Map<String, Integer> param) {
            this.param = Objects.requireNonNull(param);
        }


        @Override
        public void writeTo(JsonGenerator generator) throws IOException {
            for (Map.Entry<String, Integer> entry : this.param.entrySet()) {
                generator.writeNumberField(entry.getKey(), entry.getValue());
            }
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(EncodeBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

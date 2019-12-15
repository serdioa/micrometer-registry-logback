package de.serdioa.micrometer.core.instrument.directlogging;

import static java.util.stream.Collectors.joining;

import java.io.Console;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;


public class ConsoleSimpleMeterLogger implements DirectMeterLogger {
    private static final Pattern PATTERN_EOL_CHARACTERS = Pattern.compile("[\n\r]");

    private Function<Meter, String> meterIdPrinter;


    public ConsoleSimpleMeterLogger() {
        this(NamingConvention.identity);
    }


    public ConsoleSimpleMeterLogger(NamingConvention namingConvention) {
        this.meterIdPrinter = defaultMeterIdPrinter(namingConvention);
    }


    public ConsoleSimpleMeterLogger(Function<Meter, String> meterIdPrinter) {
        this.meterIdPrinter = Objects.requireNonNull(meterIdPrinter);
    }


    private static Function<Meter, String> defaultMeterIdPrinter(NamingConvention namingConvention) {
        return (meter) -> {
            final String conventionName = meter.getId().getConventionName(namingConvention);
            final List<Tag> conventionTags = meter.getId().getConventionTags(namingConvention);

            return conventionName + conventionTags.stream()
                    .map(t -> t.getKey() + "=" + t.getValue())
                    .collect(joining(",", "{", "}"));
        };
    }


    @Override
    public void logTimer(Timer timer, long nanoseconds) {
        printf("%s: %d\n", printMeterId(timer), nanoseconds);
    }
    
    
    private String printMeterId(Meter meter) {
        return escape(this.meterIdPrinter.apply(meter));
    }


    private void printf(String format, Object... args) {
        Console console = System.console();
        if (console != null) {
            console.printf(format, args);
        } else {
            System.out.printf(format, args);
        }
    }


    private static String escape(String s) {
        return PATTERN_EOL_CHARACTERS.matcher(s).replaceAll("");
    }
}

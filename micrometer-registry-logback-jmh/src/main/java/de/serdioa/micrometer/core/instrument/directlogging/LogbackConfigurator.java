package de.serdioa.micrometer.core.instrument.directlogging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.helpers.NOPAppender;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Accessors(fluent = true)
public class LogbackConfigurator {

    public static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %logger %msg%n";
    public static final Level DEFAULT_LEVEL = Level.INFO;
    public static final String DEFAULT_FILE_NAME = "logback.log";

    public enum Destination {
        NOOP, CONSOLE, FILE
    }

    public enum EncoderType {
        PLAIN, JSONLOGSTASH
    }

    @Setter
    private String pattern = DEFAULT_PATTERN;

    @Setter
    private Level level = DEFAULT_LEVEL;

    @Setter
    private EncoderType encoderType = EncoderType.PLAIN;

    @Setter
    private boolean asynchronous = false;

    @Setter
    private Destination destination = Destination.CONSOLE;

    @Setter
    private String fileName = DEFAULT_FILE_NAME;

    @Getter
    private LoggerContext logCtx;

    @Getter
    private Encoder<ILoggingEvent> encoder;

    @Getter
    private Appender<ILoggingEvent> appender;


    public static void main(String[] args) throws Exception {
        new LogbackConfigurator()
                .pattern("%logger %msg%n")
                .asynchronous(false)
                .encoderType(EncoderType.PLAIN)
                .destination(Destination.CONSOLE)
                .level(Level.DEBUG)
                .configure();

        writeLogMessages();

        // Give time to asynchronous logger to finish.
        Thread.sleep(1000);
    }


    private static void writeLogMessages() {
        final Logger loggerTest = LoggerFactory.getLogger("test");
        final Logger loggerTestA = LoggerFactory.getLogger("test.A");
        final Logger loggerTestB = LoggerFactory.getLogger("test.B");

        // Plain-text messages
        loggerTest.trace("logger test: TRACE");
        loggerTest.debug("logger test: DEBUG");
        loggerTest.info("logger test: INFO");
        loggerTest.warn("logger test: WARN");
        loggerTest.error("logger test: ERROR");

        loggerTestA.trace("logger test.A: TRACE");
        loggerTestA.debug("logger test.A: DEBUG");
        loggerTestA.info("logger test.A: INFO");
        loggerTestA.warn("logger test.A: WARN");
        loggerTestA.error("logger test.A: ERROR");

        loggerTestB.trace("logger test.B: TRACE");
        loggerTestB.debug("logger test.B: DEBUG");
        loggerTestB.info("logger test.B: INFO");
        loggerTestB.warn("logger test.B: WARN");
        loggerTestB.error("logger test.B: ERROR");

        // JSON messages
        StructuredArgument age = StructuredArguments.keyValue("age", 20);
        StructuredArgument name = StructuredArguments.keyValue("name", "Alice");

        loggerTest.trace("JSON logger test: TRACE", age, name);
        loggerTest.debug("JSON logger test: DEBUG", age, name);
        loggerTest.info("JSON logger test: INFO", age, name);
        loggerTest.warn("JSON logger test: WARN", age, name);
        loggerTest.error("JSON logger test: ERROR", age, name);

        loggerTestA.trace("JSON logger test.A: TRACE", age, name);
        loggerTestA.debug("JSON logger test.A: DEBUG", age, name);
        loggerTestA.info("JSON logger test.A: INFO", age, name);
        loggerTestA.warn("JSON logger test.A: WARN", age, name);
        loggerTestA.error("JSON logger test.A: ERROR", age, name);

        loggerTestB.trace("JSON logger test.B: TRACE", age, name);
        loggerTestB.debug("JSON logger test.B: DEBUG", age, name);
        loggerTestB.info("JSON logger test.B: INFO", age, name);
        loggerTestB.warn("JSON logger test.B: WARN", age, name);
        loggerTestB.error("JSON logger test.B: ERROR", age, name);

        System.out.println("logger test: TRACE is enabled ? " + loggerTest.isTraceEnabled());
        System.out.println("logger test: DEBUG is enabled ? " + loggerTest.isDebugEnabled());
        System.out.println("logger test: INFO is enabled ? " + loggerTest.isInfoEnabled());
        System.out.println("logger test: WARN is enabled ? " + loggerTest.isWarnEnabled());
        System.out.println("logger test: ERROR is enabled ? " + loggerTest.isErrorEnabled());
    }


    public LogbackConfigurator configure() {
        this.logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.logCtx.reset();

        this.encoder = encoder(this.logCtx);
        this.appender = appender(this.logCtx, this.encoder);
        root(this.logCtx, this.appender);

        return this;
    }


    public static void stop() {
        LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
        logCtx.stop();
    }


    private Encoder<ILoggingEvent> encoder(LoggerContext logCtx) {
        switch (this.encoderType) {
            case PLAIN:
                return plainEncoder(logCtx);
            case JSONLOGSTASH:
                return jsonEncoder(logCtx);
            default:
                throw new IllegalArgumentException("Unexpected encoder type: " + this.encoderType);
        }
    }


    private Encoder<ILoggingEvent> plainEncoder(LoggerContext logCtx) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(logCtx);
        encoder.setPattern(this.pattern);
        encoder.start();

        return encoder;
    }


    private Encoder<ILoggingEvent> jsonEncoder(LoggerContext logCtx) {
        JsonProviders<ILoggingEvent> providers = new JsonProviders<>();
        providers.addProvider(new LoggingEventFormattedTimestampJsonProvider());
        providers.addProvider(new LoggerNameJsonProvider());
        // providers.addProvider(new ThreadNameJsonProvider());
        providers.addProvider(new LogstashMarkersJsonProvider());
        providers.addProvider(new MessageJsonProvider());
        providers.addProvider(new ArgumentsJsonProvider());

        providers.setContext(logCtx);
        providers.start();

        LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
        encoder.setProviders(providers);
        encoder.setContext(logCtx);
        encoder.start();

        return encoder;
    }


    private Appender<ILoggingEvent> appender(LoggerContext logCtx, Encoder<ILoggingEvent> encoder) {
        Appender<ILoggingEvent> appender;
        switch (this.destination) {
            case NOOP:
                appender = noopAppender(logCtx);
                break;
            case CONSOLE:
                appender = consoleAppender(logCtx, encoder);
                break;
            case FILE:
                appender = fileAppender(logCtx, encoder);
                break;
            default:
                throw new IllegalStateException("Unexpected destination: " + this.destination);
        }

        if (this.asynchronous) {
            appender = asynchronousAppender(logCtx, appender);
        }

        return appender;
    }


    private Appender<ILoggingEvent> noopAppender(LoggerContext logCtx) {
        NOPAppender appender = new NOPAppender();
        appender.setContext(logCtx);
        appender.setName("noop");
        appender.start();

        return appender;
    }


    private Appender<ILoggingEvent> consoleAppender(LoggerContext logCtx, Encoder<ILoggingEvent> encoder) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(logCtx);
        appender.setName("console");
        appender.setEncoder(encoder);
        appender.start();

        return appender;
    }


    private Appender<ILoggingEvent> fileAppender(LoggerContext logCtx, Encoder<ILoggingEvent> encoder) {
        FileAppender<ILoggingEvent> appender = new FileAppender();
        appender.setContext(logCtx);
        appender.setName("file");
        appender.setEncoder(encoder);
        appender.setFile(this.fileName);
        appender.start();

        return appender;
    }


    private Appender<ILoggingEvent> asynchronousAppender(LoggerContext logCtx, Appender<ILoggingEvent> delegate) {
        LoggingEventAsyncDisruptorAppender asyncAppender = new LoggingEventAsyncDisruptorAppender();
        asyncAppender.setContext(logCtx);
        asyncAppender.setName("async");
        asyncAppender.addAppender(delegate);
        asyncAppender.start();

        return asyncAppender;
    }


    private void root(LoggerContext logCtx, Appender<ILoggingEvent> appender) {
        ch.qos.logback.classic.Logger log = logCtx.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        log.setLevel(this.level);
        log.addAppender(appender);
    }
}

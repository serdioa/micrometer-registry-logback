package de.serdioa.micrometer.core.instrument.directlogging;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.function.ToDoubleFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.core.lang.Nullable;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class LoggingMeterRegistry extends AbstractLoggingMeterRegistry {

    private final LoggingRegistryConfig config;


    public LoggingMeterRegistry() {
        this(LoggingRegistryConfig.DEFAULT);
    }


    public LoggingMeterRegistry(LoggingRegistryConfig config) {
        this(config, Clock.SYSTEM, new NamedThreadFactory("logging-metrics-publisher"));
    }


    private LoggingMeterRegistry(LoggingRegistryConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock, threadFactory);
        this.config = Objects.requireNonNull(config);

        this.start(threadFactory);
    }


    @Override
    protected void publish() {
        this.getMeters().stream()
                .sorted(DefaultMeterComparator.INSTANCE)
                .forEach(m -> m.use(
                this::publishGauge,
                this::publishCounter,
                this::publishTimer,
                this::publishDistributionSummary,
                this::publishLongTaskTimer,
                this::publishTimeGauge,
                this::publishFunctionCounter,
                this::publishFunctionTimer,
                this::publishMeter));
    }


    private void publishGauge(Gauge gauge) {
        LoggingMeter loggingMeter = (LoggingMeter) gauge;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonGaugeSnapshot(gauge);

        logger.info(tags, null, snapshot);
    }


    private void publishCounter(Counter counter) {
        LoggingMeter loggingMeter = (LoggingMeter) counter;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonCounterSnapshot(counter);

        logger.info(tags, null, snapshot);
    }


    private void publishTimer(Timer timer) {
        LoggingMeter loggingMeter = (LoggingMeter) timer;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonTimerSnapshot(timer);

        logger.info(tags, null, snapshot);
    }


    private void publishDistributionSummary(DistributionSummary summary) {

    }


    private void publishLongTaskTimer(LongTaskTimer longTaskTimer) {

    }


    private void publishTimeGauge(TimeGauge timeGauge) {

    }


    private void publishFunctionCounter(FunctionCounter functionCounter) {

    }


    private void publishFunctionTimer(FunctionTimer functionTimer) {

    }


    private void publishMeter(Meter meter) {

    }


    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingGauge(id, obj, valueFunction, logger, tags);
    }


    @Override
    protected Counter newCounter(Meter.Id id) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingCounter(id, this.clock, this.config.step().toMillis(), logger, tags);
    }


    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingTimer(id, this.clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                this.config.step().toMillis(), true, logger, tags);
    }
}

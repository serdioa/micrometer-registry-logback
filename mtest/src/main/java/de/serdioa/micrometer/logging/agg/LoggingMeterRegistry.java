package de.serdioa.micrometer.logging.agg;

import de.serdioa.micrometer.logging.base.DefaultMeterComparator;
import de.serdioa.micrometer.logging.base.LoggingDistributionSummary;
import de.serdioa.micrometer.logging.base.LoggingFunctionCounter;
import de.serdioa.micrometer.logging.base.LoggingFunctionTimer;
import de.serdioa.micrometer.logging.base.LoggingGauge;
import de.serdioa.micrometer.logging.base.LoggingMeter;
import de.serdioa.micrometer.logging.base.LoggingTimeGauge;
import de.serdioa.micrometer.logging.base.LoggingTimer;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import de.serdioa.micrometer.logging.base.AbstractLoggingMeterRegistry;
import de.serdioa.micrometer.logging.base.LoggingCounter;
import de.serdioa.micrometer.logging.base.LoggingLongTaskTimer;
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
        super(config, clock);
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
        LoggingMeter loggingMeter = (LoggingMeter) summary;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonDistributionSummarySnapshot(summary);

        logger.info(tags, null, snapshot);
    }


    private void publishLongTaskTimer(LongTaskTimer longTaskTimer) {
        LoggingMeter loggingMeter = (LoggingMeter) longTaskTimer;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonLongTaskTimerSnapshot(longTaskTimer);

        logger.info(tags, null, snapshot);
    }


    private void publishTimeGauge(TimeGauge timeGauge) {
        LoggingMeter loggingMeter = (LoggingMeter) timeGauge;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonTimeGaugeSnapshot(timeGauge);

        logger.info(tags, null, snapshot);
    }


    private void publishFunctionCounter(FunctionCounter functionCounter) {
        LoggingMeter loggingMeter = (LoggingMeter) functionCounter;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonFunctionCounterSnapshot(functionCounter);

        logger.info(tags, null, snapshot);
    }


    private void publishFunctionTimer(FunctionTimer functionTimer) {
        LoggingMeter loggingMeter = (LoggingMeter) functionTimer;

        Logger logger = loggingMeter.getLogger();
        Marker tags = loggingMeter.getTags();
        StructuredArgument snapshot = new JsonFunctionTimerSnapshot(functionTimer);

        logger.info(tags, null, snapshot);
    }


    private void publishMeter(Meter meter) {

    }


    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingGauge<>(id, obj, valueFunction, logger, tags);
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

        return new LoggingTimer(id, this.clock, distributionStatisticConfig, pauseDetector, this.getBaseTimeUnit(),
                this.config.step().toMillis(), true, logger, tags);
    }


    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id,
            DistributionStatisticConfig distributionStatisticConfig, double scale) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingDistributionSummary(id, this.clock, distributionStatisticConfig, scale,
                this.config.step().toMillis(), true, logger, tags);
    }


    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingLongTaskTimer(id, this.clock, this.getBaseTimeUnit(), logger, tags);
    }


    @Override
    protected <T> TimeGauge newTimeGauge(Meter.Id id, T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingTimeGauge<>(id, obj, valueFunctionUnit, valueFunction, this.getBaseTimeUnit(), logger, tags);
    }


    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingFunctionCounter<>(id, this.clock, this.config.step().toMillis(), obj, countFunction,
                logger, tags);
    }


    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction,
            ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        Logger logger = getMeterLogger(id);
        Marker tags = getTags(id);

        return new LoggingFunctionTimer<>(id, this.clock, this.config.step().toMillis(), obj, countFunction,
                totalTimeFunction, totalTimeFunctionUnit, this.getBaseTimeUnit(), logger, tags);
    }
}

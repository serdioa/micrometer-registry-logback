package de.serdioa.micrometer.spring.test;

import de.serdioa.micrometer.test.CompositeMetricsPublisher;
import de.serdioa.micrometer.test.CounterPublisher;
import de.serdioa.micrometer.test.DistributionSummaryPublisher;
import de.serdioa.micrometer.test.FunctionCounterPublisher;
import de.serdioa.micrometer.test.FunctionTimerPublisher;
import de.serdioa.micrometer.test.GaugePublisher;
import de.serdioa.micrometer.test.LongTaskTimerPublisher;
import de.serdioa.micrometer.test.TimeGaugePublisher;
import de.serdioa.micrometer.test.TimerPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "de.serdioa.micrometer.spring.test.metrics-publisher")
public class MetricsPublisherBean {

    @Setter
    public boolean counter = true;

    @Setter
    public boolean ds = true;

    @Setter
    public boolean functionCounter = true;

    @Setter
    public boolean functionTimer = true;

    @Setter
    public boolean gauge = true;

    @Setter
    public boolean longTaskTimer = true;

    @Setter
    public boolean timeGauge = true;

    @Setter
    public boolean timer = true;

    @Autowired
    private MeterRegistry registry;

    private CompositeMetricsPublisher publisher;


    @PostConstruct
    public void init() {
        this.publisher = new CompositeMetricsPublisher();

        if (this.counter) {
            this.publisher.add(new CounterPublisher(this.registry));
        }
        if (this.ds) {
            this.publisher.add(new DistributionSummaryPublisher(this.registry));
        }
        if (this.functionCounter) {
            this.publisher.add(new FunctionCounterPublisher(this.registry));
        }
        if (this.functionTimer) {
            this.publisher.add(new FunctionTimerPublisher(this.registry));
        }
        if (this.gauge) {
            this.publisher.add(new GaugePublisher(this.registry));
        }
        if (this.longTaskTimer) {
            this.publisher.add(new LongTaskTimerPublisher(this.registry));
        }
        if (this.timeGauge) {
            this.publisher.add(new TimeGaugePublisher(this.registry));
        }
        if (this.timer) {
            this.publisher.add(new TimerPublisher(this.registry));
        }

        // this.publisher.add(new JvmMetricsPublisher(this.registry));
        this.publisher.start();
    }


    @PreDestroy
    public void destroy() throws InterruptedException {
        this.publisher.stop();
    }
}

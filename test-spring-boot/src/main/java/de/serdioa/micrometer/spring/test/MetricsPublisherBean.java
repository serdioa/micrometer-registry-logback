package de.serdioa.micrometer.spring.test;

import de.serdioa.micrometer.test.CompositeMetricsPublisher;
import de.serdioa.micrometer.test.CounterPublisher;
import de.serdioa.micrometer.test.GaugePublisher;
import de.serdioa.micrometer.test.TimeGaugePublisher;
import de.serdioa.micrometer.test.TimerPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MetricsPublisherBean {

    @Autowired
    private MeterRegistry registry;

    private CompositeMetricsPublisher publisher;


    @PostConstruct
    public void init() {
        this.publisher = new CompositeMetricsPublisher();

        this.publisher.add(new CounterPublisher(this.registry));
//        this.publisher.add(new DistributionSummaryPublisher(this.registry));
//        this.publisher.add(new FunctionCounterPublisher(this.registry));
//        this.publisher.add(new FunctionTimerPublisher(this.registry));
//        this.publisher.add(new JvmMetricsPublisher(this.registry));
        this.publisher.add(new GaugePublisher(this.registry));
//        this.publisher.add(new LongTaskTimerPublisher(this.registry));
        this.publisher.add(new TimeGaugePublisher(this.registry));
        this.publisher.add(new TimerPublisher(this.registry));

        this.publisher.start();
    }


    @PreDestroy
    public void destroy() throws InterruptedException {
        this.publisher.stop();
    }
}

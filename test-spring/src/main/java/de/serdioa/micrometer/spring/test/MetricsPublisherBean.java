package de.serdioa.micrometer.spring.test;

import java.util.Map;

import de.serdioa.micrometer.test.CompositeMetricsPublisher;
import de.serdioa.micrometer.test.CounterPublisher;
import de.serdioa.micrometer.test.DistributionSummaryPublisher;
import de.serdioa.micrometer.test.FunctionCounterPublisher;
import de.serdioa.micrometer.test.FunctionTimerPublisher;
import de.serdioa.micrometer.test.GaugePublisher;
import de.serdioa.micrometer.test.LongTaskTimerPublisher;
import de.serdioa.micrometer.test.TimeGaugePublisher;
import de.serdioa.micrometer.test.TimerPublisher;
import de.serdioa.spring.properties.StructuredPropertyService;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MetricsPublisherBean {

    private static final Logger logger = LoggerFactory.getLogger(MetricsPublisherBean.class);

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private StructuredPropertyService propertyService;

    private CompositeMetricsPublisher publisher;


    @PostConstruct
    public void initialize() {
        logger.info("Initializing {}", this.getClass().getSimpleName());

        logger.info("MeterRegistry type: " + this.registry.getClass());

        Map<String, String> props = this.propertyService
                .getProperties("de.serdioa.micrometer.spring.test.MetricsPublisher");

        this.publisher = new CompositeMetricsPublisher();
        if (this.isPropertyTrue(props, "counter")) {
            this.publisher.add(new CounterPublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "ds")) {
            this.publisher.add(new DistributionSummaryPublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "functionCounter")) {
            this.publisher.add(new FunctionCounterPublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "functionTimer")) {
            this.publisher.add(new FunctionTimerPublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "gauge")) {
            this.publisher.add(new GaugePublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "longTaskTimer")) {
            this.publisher.add(new LongTaskTimerPublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "timeGauge")) {
            this.publisher.add(new TimeGaugePublisher(this.registry));
        }
        if (this.isPropertyTrue(props, "timer")) {
            this.publisher.add(new TimerPublisher(this.registry));
        }

        // this.publisher.add(new JvmMetricsPublisher(this.registry));
        this.publisher.start();

        logger.info("Initialized {}", this.getClass().getSimpleName());
    }


    @PreDestroy
    public void destroy() throws InterruptedException {
        logger.info("Destroying {}", this.getClass().getSimpleName());

        this.publisher.stop();

        logger.info("Destroyed {}", this.getClass().getSimpleName());
    }


    private boolean isPropertyTrue(Map<String, String> properties, String key) {
        String valueStr = properties.get(key);
        return Boolean.parseBoolean(valueStr);
    }
}

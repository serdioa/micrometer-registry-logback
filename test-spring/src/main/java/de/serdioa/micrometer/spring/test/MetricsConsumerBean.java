package de.serdioa.micrometer.spring.test;

import de.serdioa.micrometer.test.MetricsFormatter;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MetricsConsumerBean {

    private static final Logger logger = LoggerFactory.getLogger(MetricsConsumerBean.class);

    @Value("${de.serdioa.micrometer.spring.test.MetricsConsumer.enabled:false}")
    private boolean enabled;

    @Autowired
    private MeterRegistry registry;


    @PostConstruct
    public void initialize() {
        logger.info("Initializing {}, enabled={}", this.getClass().getSimpleName(), this.enabled);
    }


    @Scheduled(fixedRate = 5000)
    public void consumeMetrics() {
        if (this.enabled) {
            String metrics = MetricsFormatter.format(this.registry);
            System.out.println(metrics);
        }
    }
}

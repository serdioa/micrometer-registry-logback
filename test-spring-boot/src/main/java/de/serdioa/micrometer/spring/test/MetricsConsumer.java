package de.serdioa.micrometer.spring.test;

import de.serdioa.micrometer.test.MetricsFormatter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(prefix = "de.serdioa.micrometer.spring.test.MetricsConsumer", name = "enabled",
        havingValue = "true", matchIfMissing = true)
public class MetricsConsumer {

    @Autowired
    private MeterRegistry registry;


    @Scheduled(fixedRate = 5000)
    public void consumeMetrics() {
        String metrics = MetricsFormatter.format(this.registry);
        System.out.println(metrics);
    }
}

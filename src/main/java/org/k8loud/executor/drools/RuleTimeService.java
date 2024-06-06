package org.k8loud.executor.drools;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RuleTimeService {
    private final Histogram histogram;

    public RuleTimeService(CollectorRegistry registry) {
        this.histogram = Histogram.build()
                .buckets(0.5, 1, 2, 3, 5, Double.POSITIVE_INFINITY)
                .help("rule_processing_time_metric")
                .name("rule_processing_time_metric")
                .labelNames("Rule")
                .register(registry);
    }

    public void reportTime(Instant startTime, String rule) {
        Instant endTime = Instant.now();
        Instant ruleProcessingTime = endTime.minusSeconds(startTime.getEpochSecond());
        histogram.labels(rule).observe(ruleProcessingTime.getEpochSecond());
    }
}

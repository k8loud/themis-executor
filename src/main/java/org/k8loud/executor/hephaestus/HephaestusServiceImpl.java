package org.k8loud.executor.hephaestus;

import io.github.hephaestusmetrics.model.metrics.Metric;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import io.github.hephaestusmetrics.serialization.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(value="service.enabled.hephaestus", havingValue = "true", matchIfMissing = true)
public class HephaestusServiceImpl implements HephaestusService {
    private static final String METRICS_REPR_HEADER = "[ NAME | QUERY_TAG | VALUE_STRING | VALUE ]";
    private final HephaestusProperties hephaestusProperties;
    private final RestTemplate restTemplate;
    private final String hephaestusFullUrl;
    private final Translator translator = new Translator();

    public HephaestusServiceImpl(HephaestusProperties hephaestusProperties, RestTemplate restTemplate) {
        this.hephaestusProperties = hephaestusProperties;
        this.restTemplate = restTemplate;
        this.hephaestusFullUrl = hephaestusProperties.getUrl() + hephaestusProperties.getSelectedEndpoint();
    }

    @Override
    public List<Metric> queryMetrics() {
        log.info("Querying metrics");
        RawQueryResult[] rawMetrics = restTemplate.getForObject(hephaestusFullUrl, RawQueryResult[].class);
        List<Metric> metrics = Arrays.stream(Objects.requireNonNullElse(rawMetrics, new RawQueryResult[]{}))
                .map(translator::parseResult)
                .flatMap(result -> result.getMetrics().stream())
                .toList();
        log.info("===== Queried metrics =====\n{}", getMetricsRepr(metrics));
        return metrics;
    }

    @Override
    public RawQueryResult[] queryMetricsRaw() {
        return restTemplate.getForObject(hephaestusFullUrl, RawQueryResult[].class);
    }

    private String getMetricsRepr(List<Metric> metrics) {
        return String.format("%s\n%s", METRICS_REPR_HEADER, metrics.stream()
                .map(this::getMetricAsString)
                .collect(Collectors.joining("\n")));
    }

    private String getMetricAsString(Metric m) {
        return String.format("[ %s | %s | %s | %f ]", m.getName(), m.getQueryTag(), m.getValueString(), m.getValue());
    }
}

package org.k8loud.executor.drools;

import io.github.hephaestusmetrics.model.metrics.Metric;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import io.github.hephaestusmetrics.serialization.Translator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@ConditionalOnProperty(value="service.enabled.prometheus.query", havingValue = "true", matchIfMissing = true)
public class PrometheusQueryServiceImpl implements PrometheusQueryService {
    private static final String SELECTED_ENDPOINT =  "/hephaestus/metrics/selected";

    private final RestTemplate restTemplate;
    private final Translator translator = new Translator();

    private static final String BACKEND_URL = System.getenv("HEPHAESTUS_URL");

    public PrometheusQueryServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<Metric> queryMetrics() {
        String url = BACKEND_URL + SELECTED_ENDPOINT;
        RawQueryResult[] rawMetrics = restTemplate.getForObject(url, RawQueryResult[].class);
        return Arrays.stream(Objects.requireNonNullElse(rawMetrics, new RawQueryResult[]{}))
                .map(translator::parseResult)
                .flatMap(result -> result.getMetrics().stream())
                .toList();
    }

    @Override
    public RawQueryResult[] queryMetricsRaw() {
        String url = BACKEND_URL + SELECTED_ENDPOINT;
        return restTemplate.getForObject(url, RawQueryResult[].class);
    }
}
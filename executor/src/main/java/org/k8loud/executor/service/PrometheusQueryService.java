package org.k8loud.executor.service;

import io.github.hephaestusmetrics.model.metrics.Metric;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import io.github.hephaestusmetrics.serialization.Translator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrometheusQueryService {

    private final static String SELECTED_ENDPOINT =  "/hephaestus/metrics/selected";

    private final RestTemplate restTemplate;
    private final Translator translator = new Translator();

    private static String BACKEND_URL;

    public PrometheusQueryService(RestTemplateBuilder restTemplateBuilder) {
        PrometheusQueryService.BACKEND_URL = System.getenv("HEPHAESTUS_URL");
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Metric> queryMetrics() {
        String url = BACKEND_URL + SELECTED_ENDPOINT;
        RawQueryResult[] rawMetrics = restTemplate.getForObject(url, RawQueryResult[].class);
        return Arrays.stream(Objects.requireNonNullElse(rawMetrics, new RawQueryResult[]{}))
                .map(translator::parseResult)
                .flatMap(result -> result.getMetrics().stream())
                .collect(Collectors.toList());
    }

    public RawQueryResult[] queryMetricsRaw() {
        String url = BACKEND_URL + SELECTED_ENDPOINT;
        return restTemplate.getForObject(url, RawQueryResult[].class);
    }
}
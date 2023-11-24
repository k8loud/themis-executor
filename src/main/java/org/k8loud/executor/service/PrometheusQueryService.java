package org.k8loud.executor.service;

import io.github.hephaestusmetrics.model.metrics.Metric;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;

import java.util.List;

public interface PrometheusQueryService {
    List<Metric> queryMetrics();
    RawQueryResult[] queryMetricsRaw();
}

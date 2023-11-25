package org.k8loud.executor.hephaestus;

import io.github.hephaestusmetrics.model.metrics.Metric;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;

import java.util.List;

public interface HephaestusService {
    List<Metric> queryMetrics();
    RawQueryResult[] queryMetricsRaw();
}

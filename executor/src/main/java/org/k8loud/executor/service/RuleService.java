package org.k8loud.executor.service;

import data.ExecutionRS;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.model.ActionList;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RuleService {
    private final StatelessKieSession session;
    private final KubernetesClient client = new KubernetesClientBuilder().build();
    final PrometheusQueryService queryService;

    public RuleService(StatelessKieSession session, ExecutionService executionService, PrometheusQueryService queryService) {
        this.session = session;
        this.queryService = queryService;
    }

    private String mToString(Metric m) {
        return "{ " + m.getName() + " " + m.getQueryTag() + " " + m.getValue() + "}";
    }

    @Scheduled(fixedRate = 60000)
    private void getRules() {
        log.info("Pulling metrics");
        List<Metric> metrics = queryService.queryMetrics();
        log.info(metrics.stream().map(this::mToString).toList().toString());
        ActionList actionList = new ActionList();
        session.setGlobal("actions", actionList);
        session.setGlobal("client", client);
        session.execute(metrics);
        List<ExecutionRS> a = actionList.stream().map(Action::perform).toList();
        if (a.size() > 0) log.info(a.toString());
    }
}

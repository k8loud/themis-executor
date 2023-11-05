package org.k8loud.executor.service;

import data.ExecutionRS;
import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.model.ActionList;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RuleService {

    private final KubernetesService kubernetesService;
    private final StatelessKieSession session;
    final PrometheusQueryService queryService;

    public RuleService(StatelessKieSession session, ExecutionService executionService, KubernetesService kubernetesService, PrometheusQueryService queryService) {
        this.session = session;
        this.kubernetesService = kubernetesService;
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
        session.setGlobal("k8s", kubernetesService);
        session.execute(metrics);
        List<ExecutionRS> a = actionList.stream().map(Action::execute).toList();
        if (a.size() > 0) log.info(a.toString());
    }
}

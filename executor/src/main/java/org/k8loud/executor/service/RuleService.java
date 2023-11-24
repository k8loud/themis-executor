package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRS;
import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.model.ActionList;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(value="service.enabled.rule", havingValue = "true", matchIfMissing = true)
public class RuleService {
    private final KubernetesService kubernetesService;
    private final StatelessKieSession session;
    final PrometheusQueryServiceImpl queryService;

    public RuleService(StatelessKieSession session, KubernetesService kubernetesService, PrometheusQueryServiceImpl queryService) {
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
        if (!a.isEmpty()) {
            log.info(a.toString());
        }
    }
}

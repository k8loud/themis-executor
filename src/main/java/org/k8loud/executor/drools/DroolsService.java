package org.k8loud.executor.drools;

import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.hephaestus.HephaestusService;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.model.ActionList;
import org.k8loud.executor.model.ExecutionRS;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(value="service.enabled.drools", havingValue = "true", matchIfMissing = true)
public class DroolsService {
    private final DroolsProperties droolsProperties;
    private final HephaestusService hephaestusService;
    private final KubernetesService kubernetesService;
    private final KieServices kieServices = KieServices.Factory.get();

    public DroolsService(DroolsProperties droolsProperties, HephaestusService hephaestusService,
                         KubernetesService kubernetesService) {
        this.droolsProperties = droolsProperties;
        this.hephaestusService = hephaestusService;
        this.kubernetesService = kubernetesService;
    }

    private StatelessKieSession createSession() {
        log.info("Loading rules from '{}'", droolsProperties.getRulesPath());
        final long start = System.nanoTime();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newFileResource(new File(droolsProperties.getRulesPath())));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        StatelessKieSession kieSession = kieContainer.newStatelessKieSession();
        final long finish = System.nanoTime();
        log.info("Loading rules took {} ms", (finish - start) / 1_000_000);
        return kieSession;
    }

    @Scheduled(fixedRate = 60000)
    private void queryMetricsAndProcessRules() {
        StatelessKieSession session = createSession();
        List<Metric> metrics = hephaestusService.queryMetrics();
        ActionList actionList = initializeGlobals(session);
        session.execute(metrics);
        List<ExecutionRS> results = actionList.stream()
                .map(Action::execute)
                .toList();
        log.info(String.format("Results\n%s", results.stream()
                .map(ExecutionRS::toString)
                .collect(Collectors.joining("\n"))));
    }

    private ActionList initializeGlobals(StatelessKieSession session) {
        log.info("Initializing globals");
        ActionList actionList = new ActionList();
        session.setGlobal("actions", actionList);

        session.setGlobal("k8s", kubernetesService);

        return actionList;
    }
}

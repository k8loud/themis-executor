package org.k8loud.executor.drools;

import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.hephaestus.HephaestusService;
import org.k8loud.executor.model.ActionList;
import org.k8loud.executor.model.ExecutionRS;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value="service.enabled.drools", havingValue = "true", matchIfMissing = true)
public class DroolsService {
    private final DroolsProperties droolsProperties;
    private final HephaestusService hephaestusService;
    private final UsableServices usableServices;
    private final KieServices kieServices = KieServices.Factory.get();

    private StatelessKieSession kieSession;

    public ResponseEntity<List<String>> getRules() {
        KieBase base = kieSession.getKieBase();
        List<String> rules = new LinkedList<>();
        for (KiePackage kiePackage: base.getKiePackages()) {
            rules.add(new RulePackage(kiePackage.getName(), kiePackage.getRules()).toString());
        }
        return new ResponseEntity<>(rules, HttpStatus.OK);
    }

    public ResponseEntity<String> getPackage(String rulePackage) {
        KieBase base = kieSession.getKieBase();
        KiePackage kiePackage = base.getKiePackage(rulePackage);
        if (kiePackage == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new RulePackage(kiePackage.getName(), kiePackage.getRules()).toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getRule(String ruleName, String rulePackage) {
        KieBase base = kieSession.getKieBase();
        Rule rule = base.getRule(rulePackage, ruleName);
        if (rule == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rule.toString(), HttpStatus.OK);
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
        kieSession = kieContainer.newStatelessKieSession();

        final long finish = System.nanoTime();
        log.info("Loading rules took {} ms", (finish - start) / 1_000_000);
        return kieSession;
    }

    @Scheduled(fixedRateString = "${drools.query.and.process.fixed.rate.seconds}000")
    private void queryMetricsAndProcessRules() {
        log.info("========== Start session ==========");
        StatelessKieSession session = createSession();
        List<Metric> metrics = hephaestusService.queryMetrics();
        ActionList actionList = initializeGlobals(session);

        log.info("===== Execute session =====");
        session.execute(metrics);
        List<ExecutionRS> results = actionList.stream()
                .map(Action::execute)
                .toList();

        log.info("===== Actions results =====\n{}", results.stream()
                .map(ExecutionRS::toString)
                .collect(Collectors.joining("\n")));

        log.info("Next session in {} s", droolsProperties.getQueryAndProcessFixedRateSeconds());
        log.info("========== End session ==========");
    }

    private ActionList initializeGlobals(StatelessKieSession session) {
        log.info("Initializing globals");

        ActionList actionList = new ActionList();
        session.setGlobal("actions", actionList);

        session.setGlobal("usableServices", usableServices);

        return actionList;
    }
}

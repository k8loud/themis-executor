package org.k8loud.executor.drools;

import io.github.hephaestusmetrics.model.metrics.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.hephaestus.HephaestusService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@EnableAsync
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

    @Async
    @Scheduled(fixedRateString = "${drools.query.and.process.fixed.rate.seconds}000")
    void queryMetricsAndProcessRules() {
        Date sessionStartDate = new Date();
        log.info("========== Start session @ {} ==========", sessionStartDate);
        StatelessKieSession session = createSession();
        session.setGlobal("usableServices", usableServices);
        session.setGlobal("cronChecker", new CronChecker(sessionStartDate,
                droolsProperties.getQueryAndProcessFixedRateSeconds()));

        List<Metric> metrics = hephaestusService.queryMetrics();

        log.info("===== Execute session =====");
        session.execute(metrics);

        Date sessionEndDate = new Date();
        log.info("========== End session @ {}, took {} ms, next in {} s ==========", sessionEndDate,
                sessionEndDate.getTime() - sessionStartDate.getTime(),
                droolsProperties.getQueryAndProcessFixedRateSeconds());
    }
}

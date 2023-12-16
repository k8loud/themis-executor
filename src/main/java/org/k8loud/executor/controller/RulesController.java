package org.k8loud.executor.controller;

import org.k8loud.executor.drools.DroolsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@ConditionalOnProperty(value="service.enabled.drools", havingValue = "true", matchIfMissing = true)
public class RulesController {

    private final DroolsService droolsService;

    public RulesController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }


    @GetMapping(value = "/rules")
    public ResponseEntity<List<String>> getRules() {
        return droolsService.getRules();
    }

    @GetMapping(value = "/rules/{rulePackage}/{ruleName}")
    public ResponseEntity<String> getRule(@PathVariable String rulePackage, @PathVariable String ruleName) {
        return droolsService.getRule(rulePackage, ruleName);
    }

    @GetMapping(value = "/rules/{rulePackage}")
    public ResponseEntity<String> getPackage(@PathVariable String rulePackage) {
        return droolsService.getPackage(rulePackage);
    }
}

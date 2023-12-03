package org.k8loud.executor.drools;

import org.kie.api.definition.rule.Rule;

import java.util.Collection;

public record RulePackage(String name, Collection<Rule> rules) {
}

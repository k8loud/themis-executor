package org.k8loud.executor.drools;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "drools")
public class DroolsProperties {
    private String rulesPath = "rules/rules.drl";
}

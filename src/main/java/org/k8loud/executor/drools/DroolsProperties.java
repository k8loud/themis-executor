package org.k8loud.executor.drools;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "drools")
public class DroolsProperties {
    private String rulesPath = "rules/rules.drl";
    // It's already defined in application.properties, but we can't remove it from there
    // @Scheduled requires the value to be accessible at the compilation step
    private Integer queryAndProcessFixedRateSeconds = 10;
}

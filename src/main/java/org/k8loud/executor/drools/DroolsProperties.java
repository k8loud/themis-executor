package org.k8loud.executor.drools;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Data
@Component
@ConfigurationProperties(prefix = "drools")
public class DroolsProperties {
    private String rulesPath = getDefaultRulesPath();

    private static String getDefaultRulesPath() {
        String path = Path.of("rules", "test.drl").toString();
        if (!(new File(path).exists())) {
            path = Path.of("executor", path).toString();
        }
        return path;
    }
}

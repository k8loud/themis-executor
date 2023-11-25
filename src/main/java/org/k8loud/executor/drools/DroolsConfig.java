package org.k8loud.executor.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;

@Slf4j
@Configuration
public class DroolsConfig {
    private static final String RULES_DRL = System.getenv("RULES_PATH") != null ?
            System.getenv("RULES_PATH") : getDefaultRulesPath();
    private static final KieServices kieServices = KieServices.Factory.get();

    @Bean
    public KieContainer kieContainer() {
        log.info(RULES_DRL);
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newFileResource(new File(RULES_DRL)));

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    @Bean
    public StatelessKieSession kieSession(KieContainer kieContainer) {
        return kieContainer.newStatelessKieSession();
    }

    private static String getDefaultRulesPath() {
        String path = Path.of("rules", "test.drl").toString();
        if (!(new File(path).exists())) {
            path = Path.of("executor", path).toString();
        }
        return path;
    }
}

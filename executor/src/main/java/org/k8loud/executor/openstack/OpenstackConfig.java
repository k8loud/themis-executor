package org.k8loud.executor.openstack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class OpenstackConfig {

    @Bean
    @Scope("singleton")
    public OpenstackClientProvider openstackToken(@Autowired OpenstackProperties openstackProperties) {
        return new OpenstackClientProvider(openstackProperties);
    }
}

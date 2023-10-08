package org.k8loud.executor.openstack;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:openstack-config.properties")
@ConfigurationProperties(prefix = "openstack")
@Data
public class OpenstackProperties {
    public String endpoint;
    private String username;
    private String password;
    private String domainName;
    private String domainID;
    private String projectID;
    private String openstackAuth;

    public OpenstackAuth getApiConfig() {
        return OpenstackAuth.valueOf(openstackAuth);
    }
}

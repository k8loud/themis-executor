package org.k8loud.executor.openstack;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openstack")
public class OpenstackProperties {
    private String endpoint = "https://127.0.0.1:5000";
    private String username = "username";
    private String password = "password";
    private String domainName = "domainName";
    private String domainID = "domainID";
    private String projectID = "projectID";
    private String openstackAuth = "PROJECT_SCOPED";

    public OpenstackAuth getApiConfig() {
        return OpenstackAuth.valueOf(openstackAuth);
    }
}

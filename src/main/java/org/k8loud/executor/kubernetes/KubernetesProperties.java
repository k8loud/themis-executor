package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kubernetes")
public class KubernetesProperties {
    private String master; // server ex. https://150.27.9.124:6443
    private String caCertData; // certificate-authority-data
    private String clientCertData; // client-certificate-data
    private String clientKeyData; // client-key-data

    public Config toConfig() {
        // Without Config.empty() .kube/config is loaded
        return new ConfigBuilder(Config.empty())
                .withMasterUrl(master)
                .withCaCertData(caCertData)
                .withClientCertData(clientCertData)
                .withClientKeyData(clientKeyData)
                .build();
    }
}

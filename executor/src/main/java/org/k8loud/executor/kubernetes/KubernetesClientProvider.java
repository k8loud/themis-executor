package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.impl.KubernetesClientImpl;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class KubernetesClientProvider {
    private final KubernetesProperties kubernetesProperties;

    public KubernetesClientProvider(KubernetesProperties kubernetesProperties) {
        this.kubernetesProperties = kubernetesProperties;
    }

    public KubernetesClient getClient() {
        return new KubernetesClientImpl(kubernetesProperties.toConfig());
    }
}

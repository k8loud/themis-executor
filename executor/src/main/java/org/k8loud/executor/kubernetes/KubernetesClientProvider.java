package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.impl.KubernetesClientImpl;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class KubernetesClientProvider {
    private final KubernetesProperties kubernetesProperties;
    private KubernetesClient kubernetesClient;

    @PostConstruct
    private void postConstruct() {
        kubernetesClient = new KubernetesClientImpl(kubernetesProperties.toConfig());
    }

    public KubernetesClient getClient() {
        return kubernetesClient;
    }
}

package org.k8loud.executor.action.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.k8loud.executor.action.Action;

import java.util.Map;

public abstract class KubernetesAction extends Action {
    KubernetesClient client;

    public KubernetesAction(Map<String, String> params) {
        super(params);
        //TODO how to configure??
        this.client = new KubernetesClientBuilder().build();
    }

    public KubernetesAction(Map<String, String> params, KubernetesClient client) {
        super(params);
        this.client = client;
    }
}

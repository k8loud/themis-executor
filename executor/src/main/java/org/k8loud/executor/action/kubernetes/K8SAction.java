package org.k8loud.executor.action.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.k8loud.executor.action.Action;

import java.util.Map;

public abstract class K8SAction extends Action {


    KubernetesClient client;


    public K8SAction(Map<String, String> params) {
        super(params);
        //TODO how to configure??
        this.client = new KubernetesClientBuilder().build();
    }

    public K8SAction(Map<String, String> params, KubernetesClient client) {
        super(params);
        this.client = client;
    }
}

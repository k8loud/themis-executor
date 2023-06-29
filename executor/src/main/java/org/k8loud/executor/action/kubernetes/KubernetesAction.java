package org.k8loud.executor.action.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;

public abstract class KubernetesAction extends Action {
    protected KubernetesClient client;

    protected KubernetesAction(Map<String, String> params) throws ActionException {
        super(params);
        //TODO how to configure??
        this.client = new KubernetesClientBuilder().build();
    }

    protected KubernetesAction(Map<String, String> params, KubernetesClient client) throws ActionException {
        super(params);
        this.client = client;
    }
}

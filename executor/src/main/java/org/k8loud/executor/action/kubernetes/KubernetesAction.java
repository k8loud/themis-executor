package org.k8loud.executor.action.kubernetes;

import data.Params;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

public abstract class KubernetesAction extends Action {
    protected KubernetesClient client;

    protected KubernetesAction(Params params) throws ActionException {
        super(params);
        //TODO how to configure??
        this.client = new KubernetesClientBuilder().build();
    }

    protected KubernetesAction(Params params, KubernetesClient client) throws ActionException {
        super(params);
        this.client = client;
    }
}

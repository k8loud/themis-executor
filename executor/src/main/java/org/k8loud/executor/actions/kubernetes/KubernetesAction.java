package org.k8loud.executor.actions.kubernetes;

import data.Params;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;

@NoArgsConstructor
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

    protected KubernetesAction(KubernetesClient client) throws ActionException {
        this.client = client;
    }
}

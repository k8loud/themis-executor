package org.k8loud.executor.action.kubernetes;

import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.kubernetes.KubernetesService;

@Slf4j
public abstract class KubernetesAction extends Action {
    protected KubernetesService kubernetesService;
    protected String namespace;

    protected KubernetesAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params);
        this.kubernetesService = kubernetesService;
    }

    @Override
    public void unpackParams(Params params) {
        namespace = params.getRequiredParam("namespace");
        unpackAdditionalParams(params);
    }

    protected abstract void unpackAdditionalParams(Params params);
}

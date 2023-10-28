package org.k8loud.executor.action.kubernetes;

import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

@Slf4j
public class HorizontalScalingAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private String namespace;
    private int replicas;

    public HorizontalScalingAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        namespace = params.getRequiredParam("namespace");
        replicas = params.getRequiredParamAsInteger("replicas");
    }

    @Override
    protected String performKubernetesAction() throws KubernetesException  {
        return kubernetesService.scaleHorizontally(namespace, replicas, resourceType, resourceName);
    }
}

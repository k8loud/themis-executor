package org.k8loud.executor.action.kubernetes;

import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

@Slf4j
public class HorizontalScalingAction extends KubernetesAction {
    private String namespace;
    private String resourceName;
    private String resourceType;
    private int replicas;

    public HorizontalScalingAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackParams(Params params) {
        namespace = params.getRequiredParam("namespace");
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        replicas = params.getRequiredParamAsInteger("replicas");
    }

    @Override
    protected String executeBody() throws KubernetesException  {
        return kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
    }
}

package org.k8loud.executor.action.kubernetes;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;


public class DeleteResourceAction extends KubernetesAction {
    private String namespace;
    private String resourceName;
    private String resourceType;
    private Long gracePeriodSeconds;

    public DeleteResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackParams(Params params) {
        namespace = params.getRequiredParam("namespace");
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        gracePeriodSeconds = params.getOptionalParamAsLong("gracePeriodSeconds", 0L);
    }

    @Override
    public String performKubernetesAction() throws KubernetesException {
        return kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
    }
}

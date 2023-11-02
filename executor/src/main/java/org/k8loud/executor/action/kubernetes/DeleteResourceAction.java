package org.k8loud.executor.action.kubernetes;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;


public class DeleteResourceAction extends KubernetesAction {
    private String resourceType;
    private Long gracePeriodSeconds;

    public DeleteResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        resourceType = params.getRequiredParam("resourceType");
        gracePeriodSeconds = params.getOptionalParamAsLong("gracePeriodSeconds", 0L);
    }

    @Override
    public String executeBody() throws KubernetesException {
        return kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
    }
}

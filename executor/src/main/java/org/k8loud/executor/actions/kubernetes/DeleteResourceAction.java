package org.k8loud.executor.actions.kubernetes;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;


public class DeleteResourceAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private Long gracePeriodSeconds;

    public DeleteResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Builder
    public DeleteResourceAction(KubernetesService kubernetesService, String namespace, String resourceName,
                                String resourceType, Long gracePeriodSeconds) {
        super(kubernetesService, namespace);
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        gracePeriodSeconds = params.getOptionalParamAsLong("gracePeriodSeconds", 0L);
    }

    @Override
    public String executeBody() throws KubernetesException {
        return kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
    }
}

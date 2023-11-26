package org.k8loud.executor.actions.kubernetes;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.Map;

@EqualsAndHashCode
public class DeleteResourceAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private Long gracePeriodSeconds;

    public DeleteResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Builder
    public DeleteResourceAction(KubernetesService kubernetesService, String namespace,
                                String resourceName, String resourceType, Long gracePeriodSeconds) {
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
    public Map<String, String> executeBody() throws KubernetesException {
        return kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
    }
}

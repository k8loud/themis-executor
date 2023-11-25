package org.k8loud.executor.action.kubernetes;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.Map;

public class HorizontalScalingAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private int replicas;

    public HorizontalScalingAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Builder
    public HorizontalScalingAction(KubernetesService kubernetesService, String namespace,
                                   String resourceName, String resourceType, int replicas) {
        super(kubernetesService, namespace);
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.replicas = replicas;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        replicas = params.getRequiredParamAsInteger("replicas");
    }

    @Override
    protected Map<String, String> executeBody() throws KubernetesException  {
        return kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
    }
}

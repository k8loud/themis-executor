package org.k8loud.executor.actions.kubernetes;

import data.Params;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

@Slf4j
@Builder
@AllArgsConstructor
public class HorizontalScalingAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private int replicas;

    public KubernetesService kubernetesService;

    public String namespace;

    public HorizontalScalingAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
        this.kubernetesService = kubernetesService;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        replicas = params.getRequiredParamAsInteger("replicas");
    }

    @Override
    protected String executeBody() throws KubernetesException  {
        return kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
    }
}

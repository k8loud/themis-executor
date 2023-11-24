package org.k8loud.executor.actions.kubernetes;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.Map;

public class ChangeResourcesOfContainerWithinDeploymentAction extends KubernetesAction {
    private String deploymentName;
    private String containerName;
    private String limitsCpu;
    private String limitsMemory;
    private String requestsCpu;
    private String requestsMemory;

    public ChangeResourcesOfContainerWithinDeploymentAction(Params params, KubernetesService kubernetesService)
            throws ActionException {
        super(params, kubernetesService);
    }

    @Builder
    public ChangeResourcesOfContainerWithinDeploymentAction(KubernetesService kubernetesService, String namespace,
                                                            String deploymentName, String containerName, String limitsCpu,
                                                            String limitsMemory, String requestsCpu, String requestsMemory) {
        super(kubernetesService, namespace);
        this.deploymentName = deploymentName;
        this.containerName = containerName;
        this.limitsCpu = limitsCpu;
        this.limitsMemory = limitsMemory;
        this.requestsCpu = requestsCpu;
        this.requestsMemory = requestsMemory;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        this.deploymentName = params.getRequiredParam("deploymentName");
        this.containerName = params.getRequiredParam("containerName");
        this.limitsCpu = params.getRequiredParam("limitsCpu");
        this.limitsMemory = params.getRequiredParam("limitsMemory");
        this.requestsCpu = params.getRequiredParam("requestsCpu");
        this.requestsMemory = params.getRequiredParam("requestsMemory");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return kubernetesService.changeResourcesOfContainerWithinDeploymentAction(namespace, deploymentName,
                containerName, limitsCpu, limitsMemory, requestsCpu, requestsMemory);
    }
}

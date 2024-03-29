package org.k8loud.executor.actions.kubernetes;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.model.Params;

import java.util.Map;

@EqualsAndHashCode
public class ChangeResourcesOfContainerWithinPodAction extends KubernetesAction {
    private String podName;
    private String containerName;
    private String limitsCpu;
    private String limitsMemory;
    private String requestsCpu;
    private String requestsMemory;

    public ChangeResourcesOfContainerWithinPodAction(Params params, KubernetesService kubernetesService)
            throws ActionException {
        super(params, kubernetesService);
    }

    @Builder
    public ChangeResourcesOfContainerWithinPodAction(KubernetesService kubernetesService, String namespace,
                                                     String podName, String containerName, String limitsCpu,
                                                     String limitsMemory, String requestsCpu, String requestsMemory) {
        super(kubernetesService, namespace);
        this.podName = podName;
        this.containerName = containerName;
        this.limitsCpu = limitsCpu;
        this.limitsMemory = limitsMemory;
        this.requestsCpu = requestsCpu;
        this.requestsMemory = requestsMemory;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        this.podName = params.getRequiredParam("podName");
        this.containerName = params.getRequiredParam("containerName");
        this.limitsCpu = params.getRequiredParam("limitsCpu");
        this.limitsMemory = params.getRequiredParam("limitsMemory");
        this.requestsCpu = params.getRequiredParam("requestsCpu");
        this.requestsMemory = params.getRequiredParam("requestsMemory");
    }

    @Override
    protected Map<String, Object> executeBody() throws CustomException {
        return kubernetesService.changeResourcesOfContainerWithinPodAction(namespace, podName, containerName, limitsCpu,
                limitsMemory, requestsCpu, requestsMemory);
    }
}

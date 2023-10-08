package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;

@Slf4j
public class HorizontalScalingAction extends KubernetesAction {
    private String resourceName;
    private String resourceType;
    private String namespace;
    private int replicas;

    public HorizontalScalingAction(Params params) throws ActionException {
        super(params);
    }

    public HorizontalScalingAction(Params params, KubernetesClient client) throws ActionException {
        super(params, client);
    }

    @Override
    public void unpackParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        resourceType = params.getRequiredParam("resourceType");
        namespace = params.getRequiredParam("namespace");
        replicas = Integer.parseInt(params.getRequiredParam("replicas"));
    }

    @Override
    public ExecutionRS perform() {
        switch (resourceType) {
            case "ReplicaSet" ->
                    client.apps().replicaSets().inNamespace(namespace).withName(resourceName).scale(replicas);
            case "Deployment" ->
                    client.apps().deployments().inNamespace(namespace).withName(resourceName).scale(replicas);
            case "StatefulSet" ->
                    client.apps().statefulSets().inNamespace(namespace).withName(resourceName).scale(replicas);
            case "ControllerRevision" ->
                    client.apps().controllerRevisions().inNamespace(namespace).withName(resourceName).scale(replicas);
            default -> {
                return ExecutionRS.builder().result("BAAAAAAAAAD").exitCode(ExecutionExitCode.NOT_OK).build();
            }
        }

        return ExecutionRS.builder()
                .result("Resource: " + resourceType + "/" + resourceName + " scaled to: " + replicas)
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}

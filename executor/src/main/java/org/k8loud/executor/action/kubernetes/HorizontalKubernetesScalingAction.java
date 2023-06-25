package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HorizontalKubernetesScalingAction extends KubernetesAction {
    private String name;
    private String type;
    private String namespace;
    private int replicas;
    // params ----------------------------------------------------------------------------------------------------------

    public HorizontalKubernetesScalingAction(Map<String, String> params) {
        super(params);
    }

    public HorizontalKubernetesScalingAction(Map<String, String> params, KubernetesClient client) {
        super(params, client);
    }

    @Override
    public void unpackParams(Map<String, String> params) {
        name = params.get("resource-name");
        type = params.get("resource-type");
        namespace = params.get("namespace");
        replicas = Integer.parseInt(params.get("replicas"));
    }

    @Override
    public ExecutionRS perform() {
        switch (type) {
            case "ReplicaSet" -> client.apps().replicaSets().inNamespace(namespace).withName(name).scale(replicas);
            case "Deployment" -> client.apps().deployments().inNamespace(namespace).withName(name).scale(replicas);
            case "StatefulSet" -> client.apps().statefulSets().inNamespace(namespace).withName(name).scale(replicas);
            case "ControllerRevision" -> client.apps().controllerRevisions().inNamespace(namespace).withName(name).scale(replicas);
            default -> {
                return ExecutionRS.builder()
                        .result("BAAAAAAAAAD")
                        .exitCode(ExecutionExitCode.NOT_OK)
                        .build();
            }
        }

        return ExecutionRS.builder()
                .result("Resource: " + type + "/" + name + " scaled to: " + replicas)
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}

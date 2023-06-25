package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HorizontalKubernetesScalingAction extends K8SAction {


    public HorizontalKubernetesScalingAction(Map<String, String> params) {
        super(params);
    }

    public HorizontalKubernetesScalingAction(Map<String, String> params, KubernetesClient client) {
        super(params, client);
    }

    /**
     * params:
     * resource-type:
     * resource-name:
     * namespace:
     * replicas:
     */
    @Override
    public ExecutionRS perform() {
        String name = getParams().get("resource-name");
        String type = getParams().get("resource-type");
        String namespace = getParams().get("namespace");
        int replicas = Integer.parseInt(getParams().get("replicas"));




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

package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Map;

public class UpdateConfigMapAction extends KubernetesAction {
    private String namespace;
    private String resourceName;
    private Map<String, String> replacements;

    public UpdateConfigMapAction(Map<String, String> params) {
        super(params);
    }

    public UpdateConfigMapAction(Map<String, String> params, KubernetesClient client) {
        super(params, client);
    }

    @Override
    public void unpackParams(Map<String, String> params) {
        namespace = params.get("namespace");
        resourceName = params.get("resourceName");
        // FIXME more than 1 param change pls
        replacements = Map.of(params.get("k1"), params.get("v1"));
    }

    /**
     * namespace:
     * resource-name:
     * operation-type: { replace | change-value }
     * replacements: { key: value }
     *
     */

    @Override
    public ExecutionRS perform() {
        ConfigMap cm = client.configMaps()
                .inNamespace(namespace)
                .withName(resourceName)
                .edit(c -> new ConfigMapBuilder(c)
                        .addToData(replacements).build());


        return ExecutionRS.builder()
                .result("Update of config map: " + resourceName + " successful")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}

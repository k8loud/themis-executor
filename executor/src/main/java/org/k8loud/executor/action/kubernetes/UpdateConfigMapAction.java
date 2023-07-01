package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;

public class UpdateConfigMapAction extends KubernetesAction {
    private String namespace;
    private String resourceName;
    private Map<String, String> replacements;

    public UpdateConfigMapAction(Params params) throws ActionException {
        super(params);
    }

    public UpdateConfigMapAction(Params params, KubernetesClient client) throws ActionException {
        super(params, client);
    }

    @Override
    public void unpackParams(Params params) {
        namespace = params.getRequiredParam("namespace");
        resourceName = params.getRequiredParam("resourceName");
        // FIXME more than 1 param change pls
        replacements = Map.of(params.getRequiredParam("k1"), params.getRequiredParam("v1"));
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

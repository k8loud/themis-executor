package org.k8loud.executor.actions.kubernetes;

import org.k8loud.executor.model.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.Map;

public class UpdateConfigMapAction extends KubernetesAction {
    private String resourceName;
    private Map<String, String> replacements;

    public UpdateConfigMapAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    public UpdateConfigMapAction(KubernetesService kubernetesService, String namespace,
                                 String resourceName, Map<String, String> replacements) {
        super(kubernetesService, namespace);
        this.resourceName = resourceName;
        this.replacements = replacements;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        resourceName = params.getRequiredParam("resourceName");
        // FIXME more than 1 param change pls
        replacements = Map.of(params.getRequiredParam("k1"), params.getRequiredParam("v1"));
    }

    /**
     * namespace:
     * resource-name:
     * operation-type: { replace | change-value }
     * replacements: { key: value }
     */

    @Override
    public Map<String, String> executeBody() throws KubernetesException {
        return kubernetesService.updateConfigMap(namespace, resourceName, replacements);
    }
}

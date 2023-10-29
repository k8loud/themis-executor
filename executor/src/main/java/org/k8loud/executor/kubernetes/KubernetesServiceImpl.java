package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.KubernetesException;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.k8loud.executor.exception.code.KubernetesExceptionCode.RESOURCE_NOT_FOUND;
import static org.k8loud.executor.kubernetes.ResourceType.CONFIG_MAP;

@SuppressWarnings("unchecked")
@Service
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {
    private final KubernetesClientProvider clientProvider;

    public KubernetesServiceImpl(KubernetesClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public String scaleHorizontally(String namespace, String resourceName, String resourceType, Integer replicas)
            throws KubernetesException {
        getResourceThrowIfAbsent(namespace, resourceType, resourceName)
                .scale(replicas);
        return String.format("Resource '%s/%s' scaled to %s", resourceType, resourceName, replicas);
    }

    @Override
    public String deleteResource(String namespace, String resourceName, String resourceType, Long gracePeriodSeconds)
            throws KubernetesException {
        getResourceThrowIfAbsent(namespace, resourceType, resourceName)
                .withGracePeriod(gracePeriodSeconds)
                .delete();
        return String.format("Resource '%s/%s' deleted", resourceType, resourceName);
    }

    @Override
    public String updateConfigMap(String namespace, String resourceName, Map<String, String> replacements)
            throws KubernetesException {
        getResourceThrowIfAbsent(namespace, CONFIG_MAP.toString(), resourceName)
                .edit(c -> new ConfigMapBuilder((ConfigMap) c)
                        .addToData(replacements).build());
        return String.format("Update of config map '%s' successful", resourceName);
    }

    private <T> Resource<T> getResourceThrowIfAbsent(String namespace, String resourceType, String resourceName)
            throws KubernetesException {
        ResourceType resourceTypeObj = ResourceType.fromString(resourceType);
        KubernetesClient client = clientProvider.getClient();
        AppsAPIGroupDSL apps = client.apps();
        MixedOperation<T, ?, ?> mixedOperation = (MixedOperation<T, ?, ?>) switch (resourceTypeObj) {
            case REPLICA_SET -> apps.replicaSets();
            case DEPLOYMENT -> apps.deployments();
            case STATEFUL_SET -> apps.statefulSets();
            case CONTROLLER_REVISION -> apps.controllerRevisions();
            case CONFIG_MAP -> client.configMaps();
            case POD -> client.pods();
        };
        Resource<T> resource = mixedOperation.inNamespace(namespace).withName(resourceName);
        if (resource.get() == null) {
            throw new KubernetesException(RESOURCE_NOT_FOUND);
        }
        return resource;
    }
}

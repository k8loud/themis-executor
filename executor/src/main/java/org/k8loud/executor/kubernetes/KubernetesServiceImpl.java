package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.util.Util;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.k8loud.executor.exception.code.KubernetesExceptionCode.*;
import static org.k8loud.executor.kubernetes.ResourceType.CONFIG_MAP;
import static org.k8loud.executor.util.Util.getFullResourceName;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Service
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {
    private final KubernetesClientProvider clientProvider;

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "SCALE_HORIZONTALLY_FAILED")
    public String scaleHorizontally(String namespace, String resourceName, String resourceType, Integer replicas)
            throws KubernetesException {
        log.info("Scaling {} to {}", getFullResourceName(resourceType, resourceName), replicas);
        getResource(namespace, resourceType, resourceName)
                .scale(replicas);
        return String.format("Scaled %s to %d", getFullResourceName(resourceType, resourceName), replicas);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "DELETE_RESOURCE_FAILED")
    public String deleteResource(String namespace, String resourceName, String resourceType, Long gracePeriodSeconds)
            throws KubernetesException {
        log.info("Deleting {}, giving {} seconds for shutdown", getFullResourceName(resourceType, resourceName),
                gracePeriodSeconds);
        getResource(namespace, resourceType, resourceName)
                .withGracePeriod(gracePeriodSeconds)
                .delete();
        return String.format("Resource %s deleted", getFullResourceName(resourceType, resourceName));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "UPDATE_CONFIG_MAP_FAILED")
    public String updateConfigMap(String namespace, String resourceName, Map<String, String> replacements)
            throws KubernetesException {
        log.info("Updating {} with {}", getFullResourceName(CONFIG_MAP.toString(), resourceName), replacements);
        getResource(namespace, CONFIG_MAP.toString(), resourceName)
                .edit(c -> new ConfigMapBuilder((ConfigMap) c)
                        .addToData(replacements).build());
        return String.format("Update of %s successful", getFullResourceName(CONFIG_MAP.toString(), resourceName));
    }

    public <T> Resource<T> getResource(String namespace, String resourceType, String resourceName)
            throws KubernetesException {
        log.info("Looking for {} in {}", getFullResourceName(resourceType, resourceName), namespace);
        if (Util.emptyOrBlank(namespace)) {
            throw new KubernetesException(String.format("namespace '%s' is empty or blank", namespace),
                    EMPTY_OR_BLANK_NAMESPACE);
        } else if (Util.emptyOrBlank(resourceName)) {
            throw new KubernetesException(String.format("resourceName '%s' is empty or blank", resourceName),
                    EMPTY_OR_BLANK_RESOURCE_NAME);
        }
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
            throw new KubernetesException(String.format("Couldn't find '%s'", getFullResourceName(resourceType, resourceName)),
                    RESOURCE_NOT_FOUND);
        }
        return resource;
    }
}

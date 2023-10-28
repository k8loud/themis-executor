package org.k8loud.executor.kubernetes;

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

@Service
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {
    private final KubernetesClientProvider clientProvider;

    public KubernetesServiceImpl(KubernetesClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public String scaleHorizontally(String namespace, Integer replicas, String resourceType, String resourceName)
            throws KubernetesException {
        ResourceType resourceTypeObj = ResourceType.fromString(resourceType);
        // TODO: 'AppsAPIGroupDSL' used without 'try'-with-resources statement
        //  Figure out how to add the try block without closing client which breaks tests
        AppsAPIGroupDSL apps = clientProvider.getClient().apps();
        Resource<?> resource = switch (resourceTypeObj) {
            case REPLICA_SET -> getResourceThrowIfAbsent(apps.replicaSets(), namespace, resourceName);
            case DEPLOYMENT -> getResourceThrowIfAbsent(apps.deployments(), namespace, resourceName);
            case STATEFUL_SET -> getResourceThrowIfAbsent(apps.statefulSets(), namespace, resourceName);
            case CONTROLLER_REVISION -> getResourceThrowIfAbsent(apps.controllerRevisions(), namespace, resourceName);
        };
        resource.scale(replicas);
        return String.format("Resource '%s/%s' scaled to %s", resourceType, resourceName, replicas);
    }

    @Override
    public String updateConfigMap(String namespace, String resourceName, Map<String, String> replacements)
            throws KubernetesException {
        KubernetesClient client = clientProvider.getClient();
        getResourceThrowIfAbsent(client.configMaps(), namespace, resourceName)
                .edit(c -> new ConfigMapBuilder(c)
                        .addToData(replacements).build());
        return String.format("Update of config map '%s' successful", resourceName);
    }

    private <T> Resource<T> getResourceThrowIfAbsent(MixedOperation<T, ?, ?> mixedOperation, String namespace,
                                                 String resourceName) throws KubernetesException {
        Resource<T> resource = mixedOperation.inNamespace(namespace).withName(resourceName);
        if (resource.get() == null) {
            throw new KubernetesException(RESOURCE_NOT_FOUND);
        }
        return resource;
    }
}

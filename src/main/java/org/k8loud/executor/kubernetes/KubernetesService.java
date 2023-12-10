package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

public interface KubernetesService {
    Map<String, Object> scaleHorizontally(String namespace, String resourceName, String resourceType,
                                          Integer replicas) throws KubernetesException;

    <T extends HasMetadata> Map<String, Object> addResource(String namespace, String resourceType,
                                                            String resourceDescription) throws KubernetesException;

    Map<String, Object> deleteResource(String namespace, String resourceName, String resourceType,
                                       Long gracePeriodSeconds) throws KubernetesException;

    Map<String, Object> updateConfigMap(String namespace, String resourceName,
                                        Map<String, String> replacements) throws KubernetesException;

    Map<String, Object> changeResourcesOfContainerWithinPodAction(String namespace, String podName, String containerName,
                                                     String limitsCpu, String limitsMemory, String requestsCpu,
                                                     String requestsMemory) throws KubernetesException;

    Map<String, Object> changeResourcesOfContainerWithinDeploymentAction(String namespace, String deploymentName,
                                                                                String containerName, String limitsCpu,
                                                                                String limitsMemory, String requestsCpu,
                                                                                String requestsMemory)
            throws KubernetesException;

    <T> Resource<T> getResource(String namespace, String resourceType, String resourceName) throws KubernetesException;

    <T> Resource<T> loadResource(String resourceType, String resourceDescription) throws KubernetesException;
}

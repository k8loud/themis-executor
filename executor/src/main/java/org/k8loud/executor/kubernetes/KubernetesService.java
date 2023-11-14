package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

public interface KubernetesService {
    Map<String, String> scaleHorizontally(String namespace, String resourceName, String resourceType,
                                          Integer replicas) throws KubernetesException;

    <T extends HasMetadata> Map<String, String> addResource(String namespace, String resourceType,
                                                            String resourceDescription) throws KubernetesException;

    Map<String, String> deleteResource(String namespace, String resourceName, String resourceType,
                                       Long gracePeriodSeconds) throws KubernetesException;

    Map<String, String> updateConfigMap(String namespace, String resourceName,
                                        Map<String, String> replacements) throws KubernetesException;

    <T> Resource<T> getResource(String namespace, String resourceType, String resourceName) throws KubernetesException;

    <T> Resource<T> loadResource(String resourceType, String resourceDescription) throws KubernetesException;
}

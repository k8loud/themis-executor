package org.k8loud.executor.kubernetes;

import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

public interface KubernetesService {
    String scaleHorizontally(String namespace, Integer replicas, String resourceType, String resourceName) throws KubernetesException;
    String updateConfigMap(String namespace, String resourceName, Map<String, String> replacements) throws KubernetesException;
}

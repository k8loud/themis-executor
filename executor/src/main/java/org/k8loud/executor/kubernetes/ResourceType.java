package org.k8loud.executor.kubernetes;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.code.KubernetesExceptionCode;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public enum ResourceType {
    REPLICA_SET("ReplicaSet"),
    DEPLOYMENT("Deployment"),
    STATEFUL_SET("StatefulSet"),
    CONTROLLER_REVISION("ControllerRevision"),
    CONFIG_MAP("ConfigMap"),
    POD("Pod");

    private final String asString;

    ResourceType(String asString) {
        this.asString = asString;
    }

    public static ResourceType fromString(String s) throws KubernetesException {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.toString().equalsIgnoreCase(s)) {
                log.debug("{} parsed to {}", s, resourceType.name());
                return resourceType;
            }
        }
        String valid = String.format("[%s]", Arrays.stream(ResourceType.values())
                .map(ResourceType::toString)
                .collect(Collectors.joining(", ")));
        log.error("Invalid resource type {}, valid values: {}", s, valid);
        String exceptionMessage = String.format("Invalid resource type %s, valid values: %s", s, valid);
        throw new KubernetesException(exceptionMessage, KubernetesExceptionCode.INVALID_RESOURCE_TYPE);
    }

    @Override
    public String toString() {
        return asString;
    }
}

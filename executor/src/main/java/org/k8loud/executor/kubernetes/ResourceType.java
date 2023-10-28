package org.k8loud.executor.kubernetes;

import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.code.KubernetesExceptionCode;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ResourceType {
    REPLICA_SET("ReplicaSet"),
    DEPLOYMENT("Deployment"),
    STATEFUL_SET("StatefulSet"),
    CONTROLLER_REVISION("ControllerRevision");

    private final String asString;

    ResourceType(String asString) {
        this.asString = asString;
    }

    public String getAsString() {
        return asString;
    }

    public static ResourceType fromString(String s) throws KubernetesException {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.asString.equalsIgnoreCase(s)) {
                return resourceType;
            }
        }
        String exceptionMessage = String.format("Invalid resource type '%s', valid values: '%s'", s,
                Arrays.stream(ResourceType.values())
                        .map(ResourceType::getAsString)
                        .collect(Collectors.joining("', '")));
        throw new KubernetesException(exceptionMessage, KubernetesExceptionCode.INVALID_RESOURCE_TYPE);
    }
}

package org.k8loud.executor.kubernetes;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.code.KubernetesExceptionCode;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public enum KubernetesResourceType {
    REPLICA_SET("ReplicaSet"),
    DEPLOYMENT("Deployment"),
    STATEFUL_SET("StatefulSet"),
    CONTROLLER_REVISION("ControllerRevision"),
    CONFIG_MAP("ConfigMap"),
    POD("Pod");

    private final String asString;

    KubernetesResourceType(String asString) {
        this.asString = asString;
    }

    public static KubernetesResourceType fromString(String s) throws KubernetesException {
        for (KubernetesResourceType resourceType : KubernetesResourceType.values()) {
            if (resourceType.toString().equalsIgnoreCase(s)) {
                log.trace("{} parsed to {}", s, resourceType.name());
                return resourceType;
            }
        }
        String valid = String.format("[%s]", Arrays.stream(KubernetesResourceType.values())
                .map(KubernetesResourceType::toString)
                .collect(Collectors.joining(", ")));
        throw new KubernetesException(String.format("Invalid resource type '%s', valid values: %s", s, valid),
                KubernetesExceptionCode.INVALID_RESOURCE_TYPE);
    }

    @Override
    public String toString() {
        return asString;
    }
}

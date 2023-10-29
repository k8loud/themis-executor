package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.apps.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.KubernetesException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HorizontalScalingTest extends BaseTest {
    @ParameterizedTest
    @MethodSource
    void testScalingStatefulSet(String namespace, String resourceName, String resourceType, Integer replicas)
            throws KubernetesException {
        // given
        StatefulSet sts = new StatefulSetBuilder().withNewMetadata()
                .withName(resourceName)
                .withNamespace(namespace)
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(sts).create();

        // when
        kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
        StatefulSet sts1 = client.apps().statefulSets().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNotNull(sts1);
        assertNotNull(sts1.getSpec());
        assertNotNull(sts1.getStatus());
        assertEquals(replicas, sts1.getSpec().getReplicas().intValue());
    }

    @ParameterizedTest
    @MethodSource
    void testScalingDeployment(String namespace, String resourceName, String resourceType, Integer replicas)
            throws KubernetesException {
        // given
        Deployment depl = new DeploymentBuilder().withNewMetadata()
                .withName(resourceName)
                .withNamespace(namespace)
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(depl).create();

        // when
        kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
        Deployment depl1 = client.apps().deployments().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNotNull(depl1);
        assertNotNull(depl1.getSpec());
        assertNotNull(depl1.getStatus());
        assertEquals(replicas, depl1.getSpec().getReplicas().intValue());
    }

    @ParameterizedTest
    @MethodSource
    void testScalingReplicaSet(String namespace, String resourceName, String resourceType, Integer replicas)
            throws KubernetesException {

        // given
        ReplicaSet rss = new ReplicaSetBuilder().withNewMetadata()
                .withName(resourceName)
                .withNamespace(namespace)
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(2)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(rss).create();

        // when
        kubernetesService.scaleHorizontally(namespace, resourceName, resourceType, replicas);
        ReplicaSet repl = client.apps().replicaSets().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNotNull(repl);
        assertNotNull(repl.getSpec());
        assertNotNull(repl.getStatus());
        assertEquals(replicas, repl.getSpec().getReplicas().intValue());
    }

    private static Stream<Arguments> testScalingStatefulSet() {
        return  Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "StatefulSet", 3),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "StatefulSet", 1),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "StatefulSet", 2));
    }

    public static Stream<Arguments> testScalingDeployment() {
        return Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "Deployment", 3),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "Deployment", 1),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "Deployment", 2));
    }

    private static Stream<Arguments> testScalingReplicaSet() {
        return Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "ReplicaSet", 3),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "ReplicaSet", 1),
                Arguments.of(NAMESPACE, RESOURCE_NAME, "ReplicaSet", 2));
    }
}

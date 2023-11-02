package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.KubernetesException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeleteResourceTest extends KubernetesBaseTest {
    private static final Long GRACE_PERIOD_SECONDS = 5L;

    @ParameterizedTest
    @MethodSource
    void testDeletingPod(String namespace, String resourceName, String resourceType, Long gracePeriodSeconds)
            throws KubernetesException {
        // given
        Pod pod = new PodBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(resourceName)
                .withImage("nginx:1.7.9")
                .addNewPort().withContainerPort(80).endPort()
                .endContainer()
                .endSpec()
                .build();
        client.pods().inNamespace(namespace).resource(pod).create();
        assertNotNull(client.pods().inNamespace(namespace).withName(resourceName).get());

        // when
        String res = kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
        Pod pod1 = client.pods().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNull(pod1);
        assertEquals(String.format("Resource %s/%s deleted", resourceType, resourceName), res);
    }

    @ParameterizedTest
    @MethodSource
    void testDeletingDeployment(String namespace, String resourceName, String resourceType, Long gracePeriodSeconds)
            throws KubernetesException {
        // given
        Deployment deployment = new DeploymentBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata().addToLabels("app", "httpd").endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("busybox")
                .withImage("busybox")
                .withCommand("sleep","36000")
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        client.apps().deployments().inNamespace(namespace).resource(deployment).create();
        assertNotNull(client.apps().deployments().inNamespace(namespace).withName(resourceName).get());

        // when
        String res = kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
        Deployment deployment1 = client.apps().deployments().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNull(deployment1);
        assertEquals(String.format("Resource %s/%s deleted", resourceType, resourceName), res);
    }

    @ParameterizedTest
    @MethodSource
    void testDeletingConfigMap(String namespace, String resourceName, String resourceType, Long gracePeriodSeconds)
            throws KubernetesException {
        // given
        ConfigMap cm = new ConfigMapBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .addToData("1", "one")
                .addToData("2", "two")
                .addToData("3", "three")
                .build();
        client.configMaps().inNamespace(namespace).resource(cm).create();
        assertNotNull(client.configMaps().inNamespace(namespace).withName(resourceName).get());

        // when
        String res = kubernetesService.deleteResource(namespace, resourceName, resourceType, gracePeriodSeconds);
        ConfigMap cm1 = client.configMaps().inNamespace(namespace).withName(resourceName).get();

        // then
        assertNull(cm1);
        assertEquals(String.format("Resource %s/%s deleted", resourceType, resourceName), res);
    }

    private static Stream<Arguments> testDeletingPod() {
        return Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "Pod", GRACE_PERIOD_SECONDS));
    }

    private static Stream<Arguments> testDeletingDeployment() {
        return Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "Deployment", GRACE_PERIOD_SECONDS));
    }

    private static Stream<Arguments> testDeletingConfigMap() {
        return Stream.of(Arguments.of(NAMESPACE, RESOURCE_NAME, "ConfigMap", GRACE_PERIOD_SECONDS));
    }
}

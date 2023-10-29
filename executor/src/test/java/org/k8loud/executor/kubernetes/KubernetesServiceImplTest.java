package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.code.KubernetesExceptionCode;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.*;

public class KubernetesServiceImplTest extends BaseTest {
    @Test
    void testGetConfigMap() throws KubernetesException {
        // given
        ConfigMap cm = new ConfigMapBuilder().withNewMetadata().withName(RESOURCE_NAME).endMetadata()
                .build();
        client.configMaps().inNamespace(NAMESPACE).resource(cm).create();
        ConfigMap cmExpected = client.configMaps().inNamespace(NAMESPACE).withName(RESOURCE_NAME).get();
        assertNotNull(cmExpected);

        // when
        ConfigMap cm1 = (ConfigMap) kubernetesService.getResource(NAMESPACE, ResourceType.CONFIG_MAP.toString(),
                RESOURCE_NAME).get();

        // then
        assertNotNull(cm1);
        assertEquals(cmExpected, cm1);
    }

    @Test
    void testGetPod() throws KubernetesException {
        // given
        Pod pod = new PodBuilder().withNewMetadata().withName(RESOURCE_NAME).endMetadata().build();
        client.pods().inNamespace(NAMESPACE).resource(pod).create();
        Pod podExpected = client.pods().inNamespace(NAMESPACE).withName(RESOURCE_NAME).get();
        assertNotNull(podExpected);

        // when
        Pod pod1 = (Pod) kubernetesService.getResource(NAMESPACE, ResourceType.POD.toString(), RESOURCE_NAME).get();

        // then
        assertNotNull(pod1);
        assertEquals(podExpected, pod1);
    }

    @Test
    void testGetDeployment() throws KubernetesException {
        // given
        Deployment deployment = new DeploymentBuilder().withNewMetadata().withName(RESOURCE_NAME).endMetadata().build();
        client.apps().deployments().inNamespace(NAMESPACE).resource(deployment).create();
        Deployment deploymentExpected = client.apps().deployments().inNamespace(NAMESPACE).withName(RESOURCE_NAME).get();
        assertNotNull(deploymentExpected);

        // when
        Deployment deployment1 = (Deployment) kubernetesService.getResource(NAMESPACE, ResourceType.DEPLOYMENT.toString(),
                RESOURCE_NAME).get();

        // then
        assertNotNull(deployment1);
        assertEquals(deploymentExpected, deployment1);
    }

    @ParameterizedTest
    @MethodSource
    void testGetResourceShouldThrow(String namespace, String resourceType, String resourceName,
                         KubernetesExceptionCode expectedExceptionCode) {
        // when
        Throwable e = catchThrowable(() -> kubernetesService.getResource(namespace, resourceType, resourceName));

        // then
        assertThat(e).isExactlyInstanceOf(KubernetesException.class);
        assertThat(((KubernetesException) e).getExceptionCode()).isEqualTo(expectedExceptionCode);
    }

    private static Stream<Arguments> testGetResourceShouldThrow() {
        return Stream.of(Arguments.of(NAMESPACE, "StatefulSet", "", EMPTY_OR_BLANK_RESOURCE_NAME),
                Arguments.of(NAMESPACE, "ConfigMap", "           ", EMPTY_OR_BLANK_RESOURCE_NAME),
                Arguments.of("", "StatefulSet", RESOURCE_NAME, EMPTY_OR_BLANK_NAMESPACE),
                Arguments.of("  ", "ConfigMap", RESOURCE_NAME, EMPTY_OR_BLANK_NAMESPACE),
                Arguments.of(NAMESPACE, "", RESOURCE_NAME, INVALID_RESOURCE_TYPE),
                Arguments.of(NAMESPACE, "    ", RESOURCE_NAME, INVALID_RESOURCE_TYPE));
    }
}

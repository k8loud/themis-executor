package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.*;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.EMPTY_SPEC;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.RESOURCE_NOT_FOUND;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.POD;
import static org.k8loud.executor.util.Util.getFullResourceName;

@SuppressWarnings("unchecked")
public class ChangeResourcesOfContainerWithinPodTest extends KubernetesBaseTest {
    private static final String POD_NAME = "pod-123";
    private static final String CONTAINER_NAME = "nginx-123";

    private static final String INITIAL_LIMITS_CPU = "450m";
    private static final String INITIAL_LIMITS_MEMORY = "250Mi";
    private static final String INITIAL_REQUESTS_CPU = "150m";
    private static final String INITIAL_REQUESTS_MEMORY = "300Mi";

    private static final String TARGET_LIMITS_CPU = "300m";
    private static final String TARGET_LIMITS_MEMORY = "350Mi";
    private static final String TARGET_REQUESTS_CPU = "200m";
    private static final String TARGET_REQUESTS_MEMORY = "300Mi";

    private static final String POD_FULL_NAME = getFullResourceName(POD.toString(), POD_NAME);

    @Test
    void testChangingResourcesOfContainerWithinPod() throws KubernetesException {
        // given
        Pod pod = new PodBuilder().withNewMetadata().withName(POD_NAME).endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(CONTAINER_NAME)
                .withImage("nginx:1.7.9")
                .addNewPort().withContainerPort(80).endPort()
                .withResources(getInitialResources(
                ))
                .endContainer()
                .endSpec()
                .build();
        client.pods().inNamespace(NAMESPACE).resource(pod).create();

        // when
        kubernetesService.changeResourcesOfContainerWithinPodAction(NAMESPACE, POD_NAME, CONTAINER_NAME,
                TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY, TARGET_REQUESTS_CPU, TARGET_REQUESTS_MEMORY);

        // then
        final Pod pod1 = client.pods().inNamespace(NAMESPACE).withName(POD_NAME).get();
        assertNotNull(pod1);

        final Container container = pod1.getSpec().getContainers().stream()
                .filter(c -> c.getName().equals(CONTAINER_NAME))
                .findFirst()
                .orElse(null);
        assertNotNull(container);

        assertTargetResources(container);
    }

    @Test
    void testChangingResourcesOfContainerWithinNonExistentPod() throws KubernetesException {
        // when
        Throwable throwable = catchThrowable(() -> kubernetesService.changeResourcesOfContainerWithinPodAction(
                NAMESPACE, POD_NAME, CONTAINER_NAME, TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY, TARGET_REQUESTS_CPU,
                TARGET_REQUESTS_MEMORY));

        // then
        assertThat(throwable).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Couldn't find '%s'", POD_FULL_NAME);
        assertThat(((KubernetesException) throwable).getExceptionCode()).isEqualTo(RESOURCE_NOT_FOUND);
    }

    @Test
    void testChangingResourcesOfNonExistentContainerWithinPod() throws KubernetesException {
        // given
        Pod pod = new PodBuilder().withNewMetadata().withName(POD_NAME).endMetadata()
                .build();
        client.pods().inNamespace(NAMESPACE).resource(pod).create();

        // when
        Throwable throwable = catchThrowable(() -> kubernetesService.changeResourcesOfContainerWithinPodAction(
                NAMESPACE, POD_NAME, CONTAINER_NAME, TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY, TARGET_REQUESTS_CPU,
                TARGET_REQUESTS_MEMORY));

        // then
        assertThat(throwable).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Spec of %s is empty", POD_FULL_NAME);
        assertThat(((KubernetesException) throwable).getExceptionCode()).isEqualTo(EMPTY_SPEC);
    }

    private ResourceRequirements getInitialResources() {
        return new ResourceRequirementsBuilder()
                .addToLimits(Map.of("cpu", new Quantity(INITIAL_LIMITS_CPU)))
                .addToLimits(Map.of("memory", new Quantity(INITIAL_LIMITS_MEMORY)))
                .addToRequests(Map.of("cpu", new Quantity(INITIAL_REQUESTS_CPU)))
                .addToRequests(Map.of("memory", new Quantity(INITIAL_REQUESTS_MEMORY)))
                .build();
    }

    private void assertTargetResources(Container container) {
        final ResourceRequirements resources = container.getResources();
        assertEquals(resources.getLimits().get("cpu"), new Quantity(TARGET_LIMITS_CPU));
        assertEquals(resources.getLimits().get("memory"), new Quantity(TARGET_LIMITS_MEMORY));
        assertEquals(resources.getRequests().get("cpu"), new Quantity(TARGET_REQUESTS_CPU));
        assertEquals(resources.getRequests().get("memory"), new Quantity(TARGET_REQUESTS_MEMORY));
    }
}

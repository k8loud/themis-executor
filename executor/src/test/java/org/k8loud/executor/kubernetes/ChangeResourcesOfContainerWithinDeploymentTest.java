package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.EMPTY_SPEC;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.RESOURCE_NOT_FOUND;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.DEPLOYMENT;
import static org.k8loud.executor.util.Util.getFullResourceName;

@SuppressWarnings("unchecked")
public class ChangeResourcesOfContainerWithinDeploymentTest extends KubernetesBaseTest {
    private static final String DEPLOYMENT_NAME = "deployment-123";
    private static final String CONTAINER_NAME = "nginx-123";

    private static final String INITIAL_LIMITS_CPU = "450m";
    private static final String INITIAL_LIMITS_MEMORY = "250Mi";
    private static final String INITIAL_REQUESTS_CPU = "150m";
    private static final String INITIAL_REQUESTS_MEMORY = "300Mi";

    private static final String TARGET_LIMITS_CPU = "300m";
    private static final String TARGET_LIMITS_MEMORY = "350Mi";
    private static final String TARGET_REQUESTS_CPU = "200m";
    private static final String TARGET_REQUESTS_MEMORY = "300Mi";

    private static final String DEPLOYMENT_FULL_NAME = getFullResourceName(DEPLOYMENT.toString(), DEPLOYMENT_NAME);

    @Test
    void testChangingResourcesOfContainerWithinDeployment() throws KubernetesException {
        // given
        Deployment deployment = new DeploymentBuilder().withNewMetadata().withName(DEPLOYMENT_NAME).endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata().addToLabels("app", "httpd").endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(CONTAINER_NAME)
                .withImage("busybox")
                .withCommand("sleep","36000")
                .withResources(getInitialResources())
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        client.apps().deployments().inNamespace(NAMESPACE).resource(deployment).create();

        // when
        kubernetesService.changeResourcesOfContainerWithinDeploymentAction(NAMESPACE, DEPLOYMENT_NAME, CONTAINER_NAME,
                TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY, TARGET_REQUESTS_CPU, TARGET_REQUESTS_MEMORY);

        // then
        final Deployment deployment1 = client.apps().deployments().inNamespace(NAMESPACE).withName(DEPLOYMENT_NAME)
                .get();
        assertNotNull(deployment1);

        final Container container = deployment1.getSpec().getTemplate()
                .getSpec().getContainers().stream()
                .filter(c -> c.getName().equals(CONTAINER_NAME))
                .findFirst()
                .orElse(null);
        assertNotNull(container);

        assertTargetResources(container);
    }

    @Test
    void testChangingResourcesOfContainerWithinNonExistentDeployment() {
        // when
        Throwable throwable = catchThrowable(() -> kubernetesService.changeResourcesOfContainerWithinDeploymentAction(
                NAMESPACE, DEPLOYMENT_NAME, CONTAINER_NAME, TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY, TARGET_REQUESTS_CPU,
                TARGET_REQUESTS_MEMORY));

        // then
        assertThat(throwable).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Couldn't find '%s'", DEPLOYMENT_FULL_NAME);
        assertThat(((KubernetesException) throwable).getExceptionCode()).isEqualTo(RESOURCE_NOT_FOUND);
    }

    @Test
    void testChangingResourcesOfNonExistentContainerWithinDeployment() {
        // given
        Deployment deployment = new DeploymentBuilder().withNewMetadata().withName(DEPLOYMENT_NAME).endMetadata()
                .build();
        client.apps().deployments().inNamespace(NAMESPACE).resource(deployment).create();

        // when
        Throwable throwable = catchThrowable(() -> kubernetesService.changeResourcesOfContainerWithinDeploymentAction(
                NAMESPACE, DEPLOYMENT_NAME, CONTAINER_NAME, TARGET_LIMITS_CPU, TARGET_LIMITS_MEMORY,
                TARGET_REQUESTS_CPU, TARGET_REQUESTS_MEMORY));

        // then
        assertThat(throwable).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Spec of %s is empty", DEPLOYMENT_FULL_NAME);
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

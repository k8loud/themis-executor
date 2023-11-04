package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Pod;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.common.testutil.DataStorageTestUtil;
import org.k8loud.executor.exception.KubernetesException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.k8loud.executor.common.testdata.ResourceDescriptionTestData.RESOURCE_DESCRIPTION_CONFIG_MAP;
import static org.k8loud.executor.common.testdata.ResourceDescriptionTestData.RESOURCE_DESCRIPTION_POD;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.RESOURCE_ALREADY_EXISTS;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.CONFIG_MAP;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.POD;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

public class AddResourceTest extends KubernetesBaseTest {
    DataStorageTestUtil dataStorageTestUtil = new DataStorageTestUtil();

    @Override
    public void additionalSetUp() {
        doAnswer(i -> dataStorageTestUtil.store(i.getArgument(0), i.getArgument(1)))
                .when(dataStorageServiceMock).store(anyString(), anyString());
        doAnswer(i -> dataStorageTestUtil.remove(i.getArgument(0)))
                .when(dataStorageServiceMock).remove(anyString());
    }

    @Test
    void testAddingPod() throws KubernetesException {
        // given
        final String resourceName = "nginx";
        final String resourceType = POD.toString();

        // when
        String res = kubernetesService.addResource(NAMESPACE, resourceType, RESOURCE_DESCRIPTION_POD);
        Pod pod = client.pods().inNamespace(NAMESPACE).withName(resourceName).get();

        // then
        assertNotNull(pod);
        assertEquals(String.format("Added resource %s/%s", resourceType, resourceName), res);
    }

    @Test
    void testAddingConfigMap() throws KubernetesException {
        // given
        final String resourceName = "game-config";
        final String resourceType = CONFIG_MAP.toString();

        // when
        String res = kubernetesService.addResource(NAMESPACE, resourceType, RESOURCE_DESCRIPTION_CONFIG_MAP);
        ConfigMap cm = client.configMaps().inNamespace(NAMESPACE).withName(resourceName).get();

        // then
        assertNotNull(cm);
        assertEquals(String.format("Added resource %s/%s", resourceType, resourceName), res);
    }

    @Test
    void testAddingStatefulSet() throws KubernetesException {
        // given
        final String resourceName = "nginx";
        final String resourceType = POD.toString();

        // when
        String res = kubernetesService.addResource(NAMESPACE, POD.toString(), RESOURCE_DESCRIPTION_POD);
        Pod pod1 = client.pods().inNamespace(NAMESPACE).withName(resourceName).get();

        // then
        assertNotNull(pod1);
        assertEquals(String.format("Added resource %s/%s", resourceType, resourceName), res);
    }

    @Test
    void testAddExistingResource() throws KubernetesException {
        // given
        final String resourceName = "nginx";
        final String resourceType = POD.toString();
        kubernetesService.addResource(NAMESPACE, resourceType, RESOURCE_DESCRIPTION_POD);
        Pod pod = client.pods().inNamespace(NAMESPACE).withName(resourceName).get();
        assertNotNull(pod);

        // when
        Throwable throwable = catchThrowable(() ->
                kubernetesService.addResource(NAMESPACE, resourceType, RESOURCE_DESCRIPTION_POD));

        // then
        assertThat(throwable).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Resource %s/%s already exists", resourceType, resourceName);
        assertThat(((KubernetesException) throwable).getExceptionCode()).isEqualTo(RESOURCE_ALREADY_EXISTS);
    }
}

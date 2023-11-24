package org.k8loud.executor.kubernetes;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.common.testutil.DataStorageTestUtil;
import org.k8loud.executor.exception.KubernetesException;

import java.util.stream.Stream;

import static org.k8loud.executor.common.testdata.ResourceDescriptionTestData.*;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class LoadResourceTest extends KubernetesBaseTest {
    @Override
    public void additionalSetUp() {
        doAnswer(i -> DataStorageTestUtil.store(i.getArgument(0), i.getArgument(1)))
                .when(dataStorageServiceMock).store(anyString(), anyString());
        doAnswer(i -> DataStorageTestUtil.remove(i.getArgument(0)))
                .when(dataStorageServiceMock).remove(anyString());
    }

    @ParameterizedTest
    @MethodSource
    public void testDataStorageServiceInteraction(String resourceType, String resourceDescription)
            throws KubernetesException {
        // when
        kubernetesService.loadResource(resourceType, resourceDescription);

        // then
        verify(dataStorageServiceMock).store(eq(resourceType), eq(resourceDescription));
        verify(dataStorageServiceMock).remove(eq(resourceType));
    }

    private static Stream<Arguments> testDataStorageServiceInteraction() {
        return Stream.of(Arguments.of(POD.toString(), RESOURCE_DESCRIPTION_POD),
                Arguments.of(CONFIG_MAP.toString(), RESOURCE_DESCRIPTION_CONFIG_MAP),
                Arguments.of(STATEFUL_SET.toString(), RESOURCE_DESCRIPTION_STATEFUL_SET),
                Arguments.of(DEPLOYMENT.toString(), RESOURCE_DESCRIPTION_DEPLOYMENT));
    }
}

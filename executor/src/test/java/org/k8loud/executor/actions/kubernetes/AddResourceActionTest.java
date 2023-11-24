package org.k8loud.executor.actions.kubernetes;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.common.testdata.ResourceDescriptionTestData.*;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.POD;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddResourceActionTest extends KubernetesActionBaseTest {
    private static final String RESOURCE_DESCRIPTION_KEY = "resourceDescription";

    @Test
    void testResourceDescriptionWithNamespace() throws ActionException, KubernetesException {
        // given
        Params params = new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_WITH_NAMESPACE));
        when(kubernetesServiceMock.addResource(anyString(), anyString(), anyString())).thenReturn(resultMap);
        final String expectedResourceType = POD.toString();
        final String expectedNamespace = "namespace-from-resource-description";

        // when
        Action action = new AddResourceAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).addResource(eq(expectedNamespace), eq(expectedResourceType),
                eq(RESOURCE_DESCRIPTION_WITH_NAMESPACE));
        assertSuccessResponse(response);
    }

    @Test
    void testMissingNamespace() {
        // given
        // namespace is not a part of RESOURCE_DESCRIPTION_* by default (besides RESOURCE_DESCRIPTION_WITH_NAMESPACE)
        // in case it changes adjust accordingly
        Params params = new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_POD));

        // when
        Throwable e = catchThrowable(() -> new AddResourceAction(params, kubernetesServiceMock));

        // then
        assertThat(e).isExactlyInstanceOf(ActionException.class)
                .hasMessage("namespace has been neither passed as a param nor as a part of resourceDescription");
    }

    @Test
    void testNamespaceInParamsShouldHavePriorityOverResourceDescription() throws KubernetesException, ActionException {
        // given
        Params params = new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_WITH_NAMESPACE,
                NAMESPACE_KEY, NAMESPACE));
        when(kubernetesServiceMock.addResource(anyString(), anyString(), anyString())).thenReturn(resultMap);
        final String expectedResourceType = POD.toString();

        // when
        Action action = new AddResourceAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).addResource(eq(NAMESPACE), eq(expectedResourceType),
                eq(RESOURCE_DESCRIPTION_WITH_NAMESPACE));
        assertSuccessResponse(response);
    }

    @Test
    void testResourceDescriptionWithoutKind() {
        // given
        Params params = new Params(Map.of(RESOURCE_DESCRIPTION_KEY,
                RESOURCE_DESCRIPTION_WITHOUT_KIND, NAMESPACE_KEY, NAMESPACE));

        // when
        Throwable e = catchThrowable(() -> new AddResourceAction(params, kubernetesServiceMock));

        // then
        assertThat(e).isExactlyInstanceOf(ActionException.class)
                .hasMessage("kind in resourceDescription hasn't been found");
    }

    @ParameterizedTest
    @MethodSource
    void testValidParams(Params params, String expectedNamespace, String expectedResourceType,
                         String expectedResourceDescription) throws ActionException, KubernetesException {
        // given
        when(kubernetesServiceMock.addResource(anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        Action action = new AddResourceAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).addResource(eq(expectedNamespace), eq(expectedResourceType),
                eq(expectedResourceDescription));
        assertSuccessResponse(response);
    }

    private static Stream<Arguments> testValidParams() {
        return Stream.of(Arguments.of(new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_POD,
                        NAMESPACE_KEY, NAMESPACE)), NAMESPACE, "Pod", RESOURCE_DESCRIPTION_POD),
                Arguments.of(new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_CONFIG_MAP,
                        NAMESPACE_KEY, NAMESPACE)), NAMESPACE, "ConfigMap", RESOURCE_DESCRIPTION_CONFIG_MAP),
                Arguments.of(new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_STATEFUL_SET,
                        NAMESPACE_KEY, NAMESPACE)), NAMESPACE, "StatefulSet", RESOURCE_DESCRIPTION_STATEFUL_SET),
                Arguments.of(new Params(Map.of(RESOURCE_DESCRIPTION_KEY, RESOURCE_DESCRIPTION_DEPLOYMENT,
                        NAMESPACE_KEY, NAMESPACE)), NAMESPACE, "Deployment", RESOURCE_DESCRIPTION_DEPLOYMENT));
    }
}

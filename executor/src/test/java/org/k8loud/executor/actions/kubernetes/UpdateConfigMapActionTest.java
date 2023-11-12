package org.k8loud.executor.actions.kubernetes;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateConfigMapActionTest extends KubernetesActionBaseTest {
    @ParameterizedTest
    @MethodSource
    void testValidParams(Params params, Map<String, String> replacements)
            throws ActionException, KubernetesException {
        // given
        when(kubernetesServiceMock.updateConfigMap(anyString(), anyString(), anyMap())).thenReturn(RESULT);

        // when
        Action action = new UpdateConfigMapAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).updateConfigMap(eq(NAMESPACE), eq(RESOURCE_NAME), eq(replacements));
        assertSuccessResponse(response);
    }

    private static Stream<Arguments> testValidParams() {
        return Stream.of(Arguments.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, "k1", "k1", "v1", "v2")), Map.of("k1", "v2")),
                Arguments.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, "k1", "k6", "v1", "v5")), Map.of("k6", "v5"))
        );
    }
}
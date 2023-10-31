package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class UpdateConfigMapActionTest extends BaseTest {
    @ParameterizedTest
    @MethodSource
    void testIfKubernetesServiceIsCalled(Params params, Map<String, String> replacements)
            throws ActionException, KubernetesException {
        // when
        Action action = new UpdateConfigMapAction(params, kubernetesService);
        ExecutionRS rs = action.perform();

        // then
        verify(kubernetesService).updateConfigMap(NAMESPACE, RESOURCE_NAME, replacements);
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
    }

    private static Stream<Arguments> testIfKubernetesServiceIsCalled() {
        return Stream.of(Arguments.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, "k1", "k1", "v1", "v2")), Map.of("k1", "v2")),
                Arguments.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, "k1", "k6", "v1", "v5")), Map.of("k6", "v5"))
        );
    }
}
package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HorizontalScalingActionTest extends BaseTest {
    private static final String REPLICAS_KEY = "replicas";

    @ParameterizedTest
    @MethodSource
    void testIfKubernetesServiceIsCalled(Params params) throws ActionException, KubernetesException {
        // when
        Action action = new HorizontalScalingAction(params, kubernetesService);
        ExecutionRS rs = action.perform();

        // then
        verify(kubernetesService, times(1)).scaleHorizontally(params.getRequiredParam(NAMESPACE_KEY),
                params.getRequiredParam(RESOURCE_NAME_KEY), params.getRequiredParam(RESOURCE_TYPE_KEY),
                params.getRequiredParamAsInteger(REPLICAS_KEY));
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
    }

    private static Stream<Params> testIfKubernetesServiceIsCalled() {
        return Stream.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, REPLICAS_KEY, "3")),
                new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, REPLICAS_KEY, "5")));
    }
}

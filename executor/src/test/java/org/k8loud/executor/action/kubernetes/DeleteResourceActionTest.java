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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeleteResourceActionTest extends BaseTest {
    private static final String GRACE_PERIOD_SECONDS = "5";
    private static final String GRACE_PERIOD_SECONDS_KEY = "gracePeriodSeconds";

    @ParameterizedTest
    @MethodSource
    void testIfKubernetesServiceIsCalled(Params params) throws ActionException, KubernetesException {
        // when
        Action action = new DeleteResourceAction(params, kubernetesService);
        ExecutionRS rs = action.perform();

        // then
        verify(kubernetesService, times(1)).deleteResource(eq(params.getRequiredParam(NAMESPACE_KEY)),
                eq(params.getRequiredParam(RESOURCE_NAME_KEY)), eq(params.getRequiredParam(RESOURCE_TYPE_KEY)), any());
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
    }

    private static Stream<Params> testIfKubernetesServiceIsCalled() {
        return Stream.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, GRACE_PERIOD_SECONDS_KEY, GRACE_PERIOD_SECONDS)),
                new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE)));
    }
}

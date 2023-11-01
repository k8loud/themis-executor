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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteResourceActionTest extends BaseTest {
    private static final String GRACE_PERIOD_SECONDS = "5";
    private static final String GRACE_PERIOD_SECONDS_KEY = "gracePeriodSeconds";

    @ParameterizedTest
    @MethodSource
    void testIfKubernetesServiceIsCalled(Params params) throws ActionException, KubernetesException {
        // given
        when(kubernetesServiceMock.deleteResource(anyString(), anyString(), anyString(), anyLong())).thenReturn(RESULT);

        // when
        Action action = new DeleteResourceAction(params, kubernetesServiceMock);
        ExecutionRS rs = action.perform();

        // then
        verify(kubernetesServiceMock).deleteResource(eq(NAMESPACE), eq(RESOURCE_NAME), eq(RESOURCE_TYPE), any());
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertEquals(RESULT, rs.getResult());
    }

    private static Stream<Params> testIfKubernetesServiceIsCalled() {
        return Stream.of(new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE, GRACE_PERIOD_SECONDS_KEY, GRACE_PERIOD_SECONDS)),
                new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                        RESOURCE_TYPE)));
    }
}

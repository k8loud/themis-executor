package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HorizontalScalingActionTest extends BaseTest {
    private static final String REPLICAS_KEY = "replicas";

    @Test
    void testIfKubernetesServiceIsCalled() throws ActionException, KubernetesException {
        // given
        Params params = new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                RESOURCE_TYPE, REPLICAS_KEY, "3"));
        when(kubernetesService.scaleHorizontally(anyString(), anyString(), anyString(), anyInt())).thenReturn(RESULT);

        // when
        Action action = new HorizontalScalingAction(params, kubernetesService);
        ExecutionRS rs = action.perform();

        // then
        Integer replicas = params.getRequiredParamAsInteger(REPLICAS_KEY);
        verify(kubernetesService).scaleHorizontally(NAMESPACE, RESOURCE_NAME, RESOURCE_TYPE, replicas);
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertEquals(RESULT, rs.getResult());
    }
}

package org.k8loud.executor.action.kubernetes;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HorizontalScalingActionTest extends KubernetesActionBaseTest {
    private static final String REPLICAS_KEY = "replicas";

    @Test
    void testIfKubernetesServiceIsCalled() throws ActionException, KubernetesException {
        // given
        Params params = new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                RESOURCE_TYPE, REPLICAS_KEY, "3"));
        when(kubernetesServiceMock.scaleHorizontally(anyString(), anyString(), anyString(), anyInt())).thenReturn(RESULT);

        // when
        Action action = new HorizontalScalingAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).scaleHorizontally(eq(NAMESPACE), eq(RESOURCE_NAME), eq(RESOURCE_TYPE), eq(3));
        checkResponse(response);
    }
}

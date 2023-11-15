package org.k8loud.executor.actions.kubernetes;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class HorizontalScalingActionTest extends KubernetesActionBaseTest {
    private static final String REPLICAS_KEY = "replicas";

    @Test
    void testValidParams() throws ActionException, KubernetesException {
        // given
        Params params = new Params(Map.of(NAMESPACE_KEY, NAMESPACE, RESOURCE_NAME_KEY, RESOURCE_NAME, RESOURCE_TYPE_KEY,
                RESOURCE_TYPE, REPLICAS_KEY, "3"));
        when(kubernetesServiceMock.scaleHorizontally(anyString(), anyString(), anyString(), anyInt())).thenReturn(
                resultMap);

        // when
        Action action = new HorizontalScalingAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).scaleHorizontally(eq(NAMESPACE), eq(RESOURCE_NAME), eq(RESOURCE_TYPE), eq(3));
        assertSuccessResponse(response);
    }
}

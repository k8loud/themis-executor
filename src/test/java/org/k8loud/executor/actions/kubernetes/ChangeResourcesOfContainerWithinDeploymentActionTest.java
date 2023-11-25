package org.k8loud.executor.actions.kubernetes;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeResourcesOfContainerWithinDeploymentActionTest extends KubernetesActionBaseTest {
    private static final String DEPLOYMENT_NAME = "deploymentName-123";
    private static final String CONTAINER_NAME = "containerName-123";
    private static final String LIMITS_CPU = "300m";
    private static final String LIMITS_MEMORY = "350Mi";
    private static final String REQUESTS_CPU = "200m";
    private static final String REQUESTS_MEMORY = "300Mi";

    @Test
    void testValidParams() throws ActionException, KubernetesException {
        // given
        Params params = new Params(Map.of(NAMESPACE_KEY, NAMESPACE, "deploymentName", DEPLOYMENT_NAME,
                "containerName", CONTAINER_NAME, "limitsCpu", LIMITS_CPU, "limitsMemory", LIMITS_MEMORY,
                "requestsCpu", REQUESTS_CPU, "requestsMemory", REQUESTS_MEMORY));
        when(kubernetesServiceMock.changeResourcesOfContainerWithinDeploymentAction(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        Action action = new ChangeResourcesOfContainerWithinDeploymentAction(params, kubernetesServiceMock);
        ExecutionRS response = action.execute();

        // then
        verify(kubernetesServiceMock).changeResourcesOfContainerWithinDeploymentAction(eq(NAMESPACE), eq(DEPLOYMENT_NAME),
                eq(CONTAINER_NAME), eq(LIMITS_CPU), eq(LIMITS_MEMORY), eq(REQUESTS_CPU), eq(REQUESTS_MEMORY));
        assertSuccessResponse(response);
    }
}

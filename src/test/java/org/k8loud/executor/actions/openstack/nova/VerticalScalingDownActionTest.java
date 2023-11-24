package org.k8loud.executor.actions.openstack.nova;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.actions.openstack.VerticalScalingDownAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VerticalScalingDownActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "flavorId", FLAVOR_ID));

    @Test
    void testVerticalScalingAction() throws ActionException, OpenstackException {
        // given
        VerticalScalingDownAction verticalScalingAction = new VerticalScalingDownAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.resizeServerDown(anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = verticalScalingAction.execute();

        // then
        verify(openstackServiceMock).resizeServerDown(eq(REGION), eq(SERVER_ID), eq(FLAVOR_ID));
        assertSuccessResponse(response);
    }

    @ParameterizedTest
    @MethodSource
    void testVerticalScalingActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new VerticalScalingDownAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testVerticalScalingActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID, "flavorId", FLAVOR_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "flavorId", FLAVOR_ID)), "serverId"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "serverId", SERVER_ID)), "flavorId"));
    }
}

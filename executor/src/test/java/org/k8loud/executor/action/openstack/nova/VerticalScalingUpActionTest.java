package org.k8loud.executor.action.openstack.nova;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.action.openstack.VerticalScalingUpAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VerticalScalingUpActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "flavorId", FLAVOR_ID));

    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        VerticalScalingUpAction verticalScalingAction = new VerticalScalingUpAction(VALID_PARAMS, openstackServiceMock);
        when(openstackServiceMock.resizeServerUp(anyString(), anyString(), anyString())).thenReturn(RESULT);

        // when
        ExecutionRS response = verticalScalingAction.execute();

        // then
        verify(openstackServiceMock).resizeServerUp(eq(REGION), eq(SERVER_ID), eq(FLAVOR_ID));
        assertSuccessRespone(response);
    }

    @ParameterizedTest
    @MethodSource
    void testActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(() -> new VerticalScalingUpAction(invalidParams, openstackServiceMock));

        // then
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceMock);
    }

    private static Stream<Arguments> testActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID, "flavorId", FLAVOR_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "flavorId", FLAVOR_ID)), "serverId"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "serverId", SERVER_ID)), "flavorId"));
    }
}

package org.k8loud.executor.action.openstack.nova;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.VerticalScalingUpAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerticalScalingUpActionTest {
    private static final String REGION = "regionTest";
    private static final String SERVER_ID = "123-server-id-123";
    private static final String FLAVOR_ID = "123-flavor-id-123";
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "flavorId", FLAVOR_ID));
    @Mock
    OpenstackService openstackServiceMock;

    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        VerticalScalingUpAction verticalScalingAction = new VerticalScalingUpAction(VALID_PARAMS, openstackServiceMock);

        doNothing().when(openstackServiceMock).resizeServerUp(eq(REGION), eq(SERVER_ID), eq(FLAVOR_ID));

        // when
        ExecutionRS response = verticalScalingAction.perform();

        // then
        verify(openstackServiceMock).resizeServerUp(eq(REGION), eq(SERVER_ID), eq(FLAVOR_ID));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
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
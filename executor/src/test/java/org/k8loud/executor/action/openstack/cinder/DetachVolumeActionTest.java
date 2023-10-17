package org.k8loud.executor.action.openstack.cinder;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.DetachVolumeAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetachVolumeActionTest {
    private static final String REGION = "regionTest";
    private static final String SERVER_ID = "123-server-id-123";
    private static final String VOLUME_ID = "123-volume-id-123";
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "volumeId", VOLUME_ID));
    @Mock
    OpenstackServiceImpl openstackServiceImplMock;

    @Test
    void testVerticalScalingAction() throws ActionException, OpenstackException {
        // given
        DetachVolumeAction detachVolumeAction = new DetachVolumeAction(VALID_PARAMS,
                openstackServiceImplMock);

        doNothing().when(openstackServiceImplMock).detachVolume(anyString(), anyString(), anyString());

        // when
        ExecutionRS response = detachVolumeAction.perform();

        // then
        verify(openstackServiceImplMock).detachVolume(eq(REGION), eq(SERVER_ID), eq(VOLUME_ID));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }

    @ParameterizedTest
    @MethodSource
    void testVerticalScalingActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new DetachVolumeAction(invalidParams, openstackServiceImplMock));

        // then
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as " + "required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceImplMock);
    }

    private static Stream<Arguments> testVerticalScalingActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID, "volumeId", VOLUME_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "volumeId", VOLUME_ID)), "serverId"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "serverId", SERVER_ID)), "volumeId"),
                Arguments.of(
                        new Params(Collections.emptyMap()), "region"
                ));
    }
}

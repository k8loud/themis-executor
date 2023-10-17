package org.k8loud.executor.action.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackServiceImpl;
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
public class AttachVolumeActionTest {
    private static final String REGION = "regionTest";
    private static final String SERVER_ID = "123-server-id-123";
    private static final String VOLUME_ID = "123-volume-id-123";
    private static final String DEVICE = "/dev/test";
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "volumeId", VOLUME_ID, "device", DEVICE));
    @Mock
    OpenstackServiceImpl openstackServiceImplMock;

    @Test
    void testAttachVolumeAction() throws ActionException, OpenstackException {
        // given
        AttachVolumeAction attachVolumeAction = new AttachVolumeAction(VALID_PARAMS,
                openstackServiceImplMock);

        doNothing().when(openstackServiceImplMock).attachVolume(anyString(), anyString(), anyString(), anyString());

        // when
        ExecutionRS response = attachVolumeAction.perform();

        // then
        verify(openstackServiceImplMock).attachVolume(eq(REGION), eq(SERVER_ID), eq(VOLUME_ID), eq(DEVICE));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }

    @ParameterizedTest
    @MethodSource
    void testAttachVolumeActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new AttachVolumeAction(invalidParams, openstackServiceImplMock));

        // then
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as " + "required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceImplMock);
    }

    private static Stream<Arguments> testAttachVolumeActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID, "volumeId", VOLUME_ID, "device", DEVICE)), "region"),
                Arguments.of(
                        new Params(Map.of("volumeId", VOLUME_ID, "device", DEVICE)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "serverId", SERVER_ID)), "volumeId"));
    }
}

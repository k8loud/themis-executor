package org.k8loud.executor.actions.openstack.cinder;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.AttachVolumeAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AttachVolumeActionTest extends OpenstackActionBaseTest {
    private static final String DEVICE = "/dev/test";
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "volumeId", VOLUME_ID, "device", DEVICE));

    @Test
    void testAttachVolumeAction() throws ActionException, OpenstackException {
        // given
        AttachVolumeAction attachVolumeAction = new AttachVolumeAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.attachVolume(anyString(), anyString(), anyString(), anyString())).thenReturn(
                resultMap);

        // when
        ExecutionRS response = attachVolumeAction.execute();

        // then
        verify(openstackServiceMock).attachVolume(eq(REGION), eq(SERVER_ID), eq(VOLUME_ID), eq(DEVICE));
        assertSuccessResponse(response);
    }

    @ParameterizedTest
    @MethodSource
    void testAttachVolumeActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new AttachVolumeAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
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

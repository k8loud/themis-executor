package org.k8loud.executor.action.openstack.cinder;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.DetachVolumeAction;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DetachVolumeActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "serverId", SERVER_ID, "volumeId", VOLUME_ID));

    @Test
    void testDetachSuccess() throws ActionException, OpenstackException {
        // given
        DetachVolumeAction detachVolumeAction = new DetachVolumeAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.detachVolume(anyString(), anyString(), anyString())).thenReturn(RESULT);

        // when
        ExecutionRS response = detachVolumeAction.execute();

        // then
        verify(openstackServiceMock).detachVolume(eq(REGION), eq(SERVER_ID), eq(VOLUME_ID));
        assertSuccessResponse(response);
    }

    @ParameterizedTest
    @MethodSource
    void testDetachActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new DetachVolumeAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testDetachActionWrongParams() {
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

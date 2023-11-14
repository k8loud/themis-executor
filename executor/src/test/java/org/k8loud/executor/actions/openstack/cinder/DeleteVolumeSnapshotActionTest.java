package org.k8loud.executor.actions.openstack.cinder;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.DeleteVolumeSnapshotAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteVolumeSnapshotActionTest extends OpenstackActionBaseTest {
    @ParameterizedTest
    @MethodSource
    void testDeleteVolumeSnapshotSuccess(Params params) throws ActionException, OpenstackException {
        // given
        DeleteVolumeSnapshotAction deleteVolumeSnapshotAction = new DeleteVolumeSnapshotAction(
                params, openstackServiceMock);
        when(openstackServiceMock.deleteTheOldestVolumeSnapshot(anyString(), anyString(), anyBoolean()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = deleteVolumeSnapshotAction.execute();

        // then
        boolean keepOneSnapshot = Boolean.parseBoolean(Optional.ofNullable(params.getParams()
                .getOrDefault("keepOneSnapshot", "true")).orElse("true"));
        verify(openstackServiceMock).deleteTheOldestVolumeSnapshot(eq(REGION), eq(VOLUME_ID), eq(keepOneSnapshot));
        assertSuccessResponse(response);
    }

    private static Stream<Params> testDeleteVolumeSnapshotSuccess() {
        return Stream.of(
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID)),
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID, "keepOneSnapshot", "false")),
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID, "keepOneSnapshot", "invalid"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDeleteVolumeSnapshotWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new DeleteVolumeSnapshotAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testDeleteVolumeSnapshotWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("volumeId", VOLUME_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "keepOneSnapshot", "false")), "volumeId"),
                Arguments.of(
                        new Params(Map.of("volumeId", VOLUME_ID, "keepOneSnapshot", "false")), "region"),
                Arguments.of(
                        new Params(Collections.emptyMap()), "region"
                ));
    }
}

package org.k8loud.executor.actions.openstack.cinder;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.CreateVolumeSnapshotAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CreateVolumeSnapshotActionTest extends OpenstackActionBaseTest {
    @ParameterizedTest
    @MethodSource
    void testCreateVolumeSnapshot(Params params) throws ActionException, OpenstackException, ValidationException {
        // given
        CreateVolumeSnapshotAction createVolumeSnapshotAction = new CreateVolumeSnapshotAction(
                params, openstackServiceMock);
        when(openstackServiceMock.createVolumeSnapshot(anyString(), anyString(), nullable(String.class)))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = createVolumeSnapshotAction.execute();

        // then
        String snapshotName = params.get("snapshotName");
        verify(openstackServiceMock).createVolumeSnapshot(eq(REGION), eq(VOLUME_ID), eq(snapshotName));
        assertSuccessResponse(response);
    }

    private static Stream<Params> testCreateVolumeSnapshot() {
        return Stream.of(
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID)),
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID)),
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID, "snapshotName", "null")),
                new Params(Map.of("region", REGION, "volumeId", VOLUME_ID, "snapshotName", "mySnapshot"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateVolumeSnapshotWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new CreateVolumeSnapshotAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testCreateVolumeSnapshotWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("volumeId", VOLUME_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "snapshotName", "anything")), "volumeId"),
                Arguments.of(
                        new Params(Map.of("snapshotName", "anything", "volumeId", VOLUME_ID)), "region"),
                Arguments.of(
                        new Params(Collections.emptyMap()), "region"
                ));
    }
}

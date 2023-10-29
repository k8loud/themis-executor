package org.k8loud.executor.action.openstack.cinder;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.CreateVolumeSnapshotAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateVolumeSnapshotActionTest {
    private static final String REGION = "regionTest";
    private static final String VOLUME_ID = "123-volume-id-123";
    @Mock
    OpenstackService openstackServiceMock;

    @ParameterizedTest
    @MethodSource
    void testCreateVolumeSnapshot(Params params) throws ActionException, OpenstackException {
        // given
        CreateVolumeSnapshotAction createVolumeSnapshotAction = new CreateVolumeSnapshotAction(
                params, openstackServiceMock);

        doNothing().when(openstackServiceMock).createVolumeSnapshot(anyString(), anyString(), nullable(String.class));

        // when
        ExecutionRS response = createVolumeSnapshotAction.perform();

        // then
        String snapshotName = params.getParams().get("snapshotName");
        verify(openstackServiceMock).createVolumeSnapshot(eq(REGION), eq(VOLUME_ID), eq(snapshotName));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }

    private static Stream<Params> testCreateVolumeSnapshot(){
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
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as " + "required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceMock);
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

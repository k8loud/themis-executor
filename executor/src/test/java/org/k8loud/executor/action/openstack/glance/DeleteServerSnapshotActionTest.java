package org.k8loud.executor.action.openstack.glance;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.DeleteServerSnapshotAction;
import org.k8loud.executor.action.openstack.DeleteVolumeSnapshotAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteServerSnapshotActionTest {
    private static final String REGION = "regionTest";
    private static final String SERVER_ID = "123-server-id-123";

    @Mock
    OpenstackService openstackServiceMock;

    @ParameterizedTest
    @MethodSource
    void testDeleteServerSnapshotSuccess(Params params) throws ActionException, OpenstackException {
        // given
        DeleteServerSnapshotAction deleteServerSnapshotAction = new DeleteServerSnapshotAction(
                params, openstackServiceMock);

        doNothing().when(openstackServiceMock).deleteTheOldestServerSnapshot(anyString(), anyString(), anyBoolean());

        // when
        ExecutionRS response = deleteServerSnapshotAction.perform();

        // then
        boolean keepOneSnapshot = Boolean.parseBoolean(Optional.ofNullable(params.getParams()
                .get("keepOneSnapshot")).orElse("true"));
        verify(openstackServiceMock).deleteTheOldestServerSnapshot(eq(REGION), eq(SERVER_ID), eq(keepOneSnapshot));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }

    private static Stream<Params> testDeleteServerSnapshotSuccess() {
        return Stream.of(
                new Params(Map.of("region", REGION, "serverId", SERVER_ID)),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "keepOneSnapshot", "false")),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "keepOneSnapshot", "invalid"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDeleteServerSnapshotWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new DeleteServerSnapshotAction(invalidParams, openstackServiceMock));

        // then
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as " + "required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceMock);
    }

    private static Stream<Arguments> testDeleteServerSnapshotWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "keepOneSnapshot", "false")), "serverId"),
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID, "keepOneSnapshot", "false")), "region"),
                Arguments.of(
                        new Params(Collections.emptyMap()), "region"
                ));
    }
}

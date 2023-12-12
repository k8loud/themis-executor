package org.k8loud.executor.actions.openstack.glance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.DeleteServerSnapshotAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteServerSnapshotActionTest extends OpenstackActionBaseTest {
    @ParameterizedTest
    @MethodSource
    void testDeleteServerSnapshotSuccess(Params params) throws ActionException, OpenstackException, ValidationException {
        // given
        DeleteServerSnapshotAction deleteServerSnapshotAction = new DeleteServerSnapshotAction(
                params, openstackServiceMock);
        when(openstackServiceMock.deleteTheOldestServerSnapshot(anyString(), anyString(), anyBoolean()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = deleteServerSnapshotAction.execute();

        // then
        boolean keepOneSnapshot = Boolean.parseBoolean(Optional.ofNullable(params
                .get("keepOneSnapshot")).orElse("true"));
        verify(openstackServiceMock).deleteTheOldestServerSnapshot(eq(REGION), eq(SERVER_ID), eq(keepOneSnapshot));
        assertSuccessResponse(response);
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
        assertMissingParamException(throwable, missingParam);
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

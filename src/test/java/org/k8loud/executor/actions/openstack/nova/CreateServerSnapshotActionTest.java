package org.k8loud.executor.actions.openstack.nova;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.CreateServerSnapshotAction;
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

public class CreateServerSnapshotActionTest extends OpenstackActionBaseTest {
    @ParameterizedTest
    @MethodSource
    void testCreateServerSnapshotSuccess(Params params) throws ActionException, OpenstackException, ValidationException {
        // given
        CreateServerSnapshotAction createServerSnapshotAction = new CreateServerSnapshotAction(
                params, openstackServiceMock);
        when(openstackServiceMock.createServerSnapshot(anyString(), anyString(), nullable(String.class), anyBoolean()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = createServerSnapshotAction.execute();

        // then
        String snapshotName = params.get("snapshotName");
        boolean stopInstance = Boolean.parseBoolean(Optional.ofNullable(params
                .get("stopInstance")).orElse("false"));

        verify(openstackServiceMock).createServerSnapshot(eq(REGION), eq(SERVER_ID), eq(snapshotName),
                eq(stopInstance));
        assertSuccessResponse(response);
    }

    private static Stream<Params> testCreateServerSnapshotSuccess() {
        return Stream.of(
                new Params(Map.of("region", REGION, "serverId", SERVER_ID)),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "stopInstance", "true")),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "snapshotName", "null")),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "snapshotName", "mySnapshot", "stopInstance",
                        "notTrue"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateServerSnapshotWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new CreateServerSnapshotAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testCreateServerSnapshotWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "snapshotName", "anything")), "serverId"),
                Arguments.of(
                        new Params(Map.of("snapshotName", "anything", "serverId", SERVER_ID)), "region"),
                Arguments.of(
                        new Params(Collections.emptyMap()), "region"
                ));
    }
}

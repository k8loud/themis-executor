package org.k8loud.executor.actions.openstack.nova;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.CreateServerSnapshotAction;
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
public class CreateServerSnapshotActionTest {
    private static final String REGION = "regionTest";
    private static final String SERVER_ID = "123-server-id-123";
    @Mock
    OpenstackService openstackServiceMock;

    @ParameterizedTest
    @MethodSource
    void testCreateServerSnapshotSuccess(Params params) throws ActionException, OpenstackException {
        // given
        CreateServerSnapshotAction createServerSnapshotAction = new CreateServerSnapshotAction(
                params, openstackServiceMock);

        doNothing().when(openstackServiceMock).createServerSnapshot(
                anyString(), anyString(), nullable(String.class), anyBoolean());

        // when
        ExecutionRS response = createServerSnapshotAction.perform();

        // then
        String snapshotName = params.getParams().get("snapshotName");
        boolean stopInstance = Boolean.parseBoolean(Optional.ofNullable(params.getParams()
                .get("stopInstance")).orElse("false"));

        verify(openstackServiceMock).createServerSnapshot(eq(REGION), eq(SERVER_ID), eq(snapshotName), eq(stopInstance));
        assertThat(response.getResult()).isEqualTo("Success");
        assertThat(response.getExitCode()).isSameAs(ExecutionExitCode.OK);
    }

    private static Stream<Params> testCreateServerSnapshotSuccess(){
        return Stream.of(
                new Params(Map.of("region", REGION, "serverId", SERVER_ID)),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "stopInstance", "true")),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "snapshotName", "null")),
                new Params(Map.of("region", REGION, "serverId", SERVER_ID, "snapshotName", "mySnapshot", "stopInstance", "notTrue"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCreateServerSnapshotWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new CreateServerSnapshotAction(invalidParams, openstackServiceMock));

        // then
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as " + "required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);

        verifyNoInteractions(openstackServiceMock);
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

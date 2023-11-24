package org.k8loud.executor.actions.openstack.nova;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.actions.openstack.PauseServerAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PauseServerActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(Map.of("region", REGION, "serverId", SERVER_ID));

    @Test
    void testPauseServerAction() throws ActionException, OpenstackException {
        // given
        PauseServerAction pauseServerAction = new PauseServerAction(VALID_PARAMS, openstackServiceMock);
        when(openstackServiceMock.pauseServer(anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = pauseServerAction.execute();

        // then
        verify(openstackServiceMock).pauseServer(eq(REGION), eq(SERVER_ID));
        assertSuccessResponse(response);
    }

    @ParameterizedTest
    @MethodSource
    void testPauseServerActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new PauseServerAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testPauseServerActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("serverId", SERVER_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION)), "serverId"),
                Arguments.of(
                        new Params(Map.of()), "region"));
    }
}

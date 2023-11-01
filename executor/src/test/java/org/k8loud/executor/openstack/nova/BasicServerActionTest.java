package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.code.OpenstackExceptionCode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.UNSUPPORTED_ACTION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicServerActionTest extends OpenstackNovaBaseTest {
    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE", "STOP", "START"})
    void testSupportedActionsSuccess(Action action) throws OpenstackException {
        // given
        conditionalSetUp();
        when(serverService.action(anyString(), any(Action.class))).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.basicServerAction(server, action, clientV3Mock);

        // then
        verify(serverService).action(eq(SERVER_ID), eq(action));
        verify(server).getName();
        verify(server).getId();
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE", "STOP", "START"})
    void testSupportedActionsFailed(Action action) throws OpenstackException {
        // given
        conditionalSetUp();
        when(serverService.action(anyString(), any(Action.class)))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(
                () -> openstackNovaService.basicServerAction(server, action, clientV3Mock));

        // then
        verify(serverService).action(eq(SERVER_ID), eq(action));
        verify(server, times(2)).getName();
        verify(server).getId();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode())
                .isSameAs(OpenstackExceptionCode.getNovaExceptionCode(action));
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE", "STOP", "START"}, mode = EnumSource.Mode.EXCLUDE)
    void testUnsupportedActions(Action action) {
        // given
        when(server.getName()).thenReturn(SERVER_NAME);

        // when
        Throwable throwable = catchThrowable(
                () -> openstackNovaService.basicServerAction(server, action, clientV3Mock));

        // then
        verifyNoInteractions(serverService);
        verify(server).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(action.name() + " not supported");
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(UNSUPPORTED_ACTION);
    }

    @Override
    protected void baseSetUp() {
        // testUnsupportedActions omits baseSetUp
    }

    @Override
    protected void setUp() {
    }

    private void conditionalSetUp() {
        when(clientV3Mock.compute()).thenReturn(computeService);
        when(computeService.servers()).thenReturn(serverService);
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getName()).thenReturn(SERVER_NAME);
    }
}

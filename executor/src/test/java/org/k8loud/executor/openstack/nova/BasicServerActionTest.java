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
        supportedActionSetUp();
        when(serverServiceMock.action(anyString(), any(Action.class))).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.basicServerAction(serverMock, action, clientV3Mock);

        // then
        verify(serverServiceMock).action(eq(SERVER_ID), eq(action));
        verify(serverMock).getName();
        verify(serverMock).getId();
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE", "STOP", "START"})
    void testSupportedActionsFailed(Action action) throws OpenstackException {
        // given
        supportedActionSetUp();
        when(serverServiceMock.action(anyString(), any(Action.class)))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(
                () -> openstackNovaService.basicServerAction(serverMock, action, clientV3Mock));

        // then
        verify(serverServiceMock).action(eq(SERVER_ID), eq(action));
        verify(serverMock, times(2)).getName();
        verify(serverMock).getId();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode())
                .isSameAs(OpenstackExceptionCode.getNovaExceptionCode(action));
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE", "STOP", "START"}, mode = EnumSource.Mode.EXCLUDE)
    void testUnsupportedActions(Action action) {
        // given
        when(serverMock.getName()).thenReturn(SERVER_NAME);

        // when
        Throwable throwable = catchThrowable(
                () -> openstackNovaService.basicServerAction(serverMock, action, clientV3Mock));

        // then
        verifyNoInteractions(serverServiceMock);
        verify(serverMock).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(action.name() + " not supported");
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(UNSUPPORTED_ACTION);
    }

    private void supportedActionSetUp() {
        when(clientV3Mock.compute()).thenReturn(computeServiceMock);
        when(computeServiceMock.servers()).thenReturn(serverServiceMock);
        when(serverMock.getId()).thenReturn(SERVER_ID);
        when(serverMock.getName()).thenReturn(SERVER_NAME);
    }

    @Override
    protected void baseSetUp() {
        // testUnsupportedActions omits baseSetUp
    }

    @Override
    protected void setUp() {
    }
}

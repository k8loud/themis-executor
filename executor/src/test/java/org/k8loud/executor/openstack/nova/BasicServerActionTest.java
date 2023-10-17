package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.code.OpenstackExceptionCode;
import org.k8loud.executor.openstack.OpenstackNovaService;
import org.k8loud.executor.openstack.OpenstackNovaServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.UNSUPPORTED_ACTION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicServerActionTest {
    private static final String SERVER_NAME = "new-server-name";
    private static final String SERVER_ID = "serverId";
    private static final String EXCEPTION_MESSAGE = "Whatever message";


    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    ComputeService computeService;
    @Mock
    ServerService serverService;
    OpenstackNovaService openstackNovaService = new OpenstackNovaServiceImpl();

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE"})
    void testSupportedActionsSuccess(Action action) throws OpenstackException {
        // given
        setup();
        when(serverService.action(anyString(), any(Action.class))).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.basicServerAction(server, action, clientV3Mock);

        // then
        verify(serverService).action(eq(SERVER_ID), eq(action));
        verify(server).getName();
        verify(server).getId();
    }

    @ParameterizedTest
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE"})
    void testSupportedActionsFailed(Action action) throws OpenstackException {
        // given
        setup();
        when(serverService.action(anyString(), any(Action.class)))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(() -> openstackNovaService.basicServerAction(server, action, clientV3Mock));

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
    @EnumSource(value = Action.class, names = {"PAUSE", "UNPAUSE"}, mode = EnumSource.Mode.EXCLUDE)
    void testUnsupportedActions(Action action) {
        // given
        when(server.getName()).thenReturn(SERVER_NAME);

        // when
        Throwable throwable = catchThrowable(() -> openstackNovaService.basicServerAction(server, action, clientV3Mock));

        // then
        verifyNoInteractions(serverService);
        verify(server).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(action.name() + " not supported");
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(UNSUPPORTED_ACTION);
    }

    public void setup() {
        when(clientV3Mock.compute()).thenReturn(computeService);
        when(computeService.servers()).thenReturn(serverService);
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getName()).thenReturn(SERVER_NAME);
    }
}

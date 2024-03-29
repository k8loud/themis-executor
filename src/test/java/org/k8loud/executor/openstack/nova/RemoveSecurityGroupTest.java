package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.model.common.ActionResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.REMOVE_SECURITY_GROUP_FROM_INSTANCE_FAILED;
import static org.mockito.Mockito.*;

public class RemoveSecurityGroupTest extends OpenstackNovaBaseTest {
    @Override
    public void setUp() {
        when(serverMock.getName()).thenReturn(SERVER_NAME);
        when(serverMock.getId()).thenReturn(SERVER_ID);
        when(securityGroupMock.getName()).thenReturn(SECURITY_GROUP_NAME);
        when(securityGroupMock.getId()).thenReturn(SECURITY_GROUP_ID);
    }

    @Test
    void testSuccess() throws OpenstackException {
        // given
        when(serverServiceMock.removeSecurityGroup(anyString(), anyString())).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.removeSecurityGroupFromInstance(serverMock, securityGroupMock, clientV3Mock);

        // then
        verify(serverServiceMock).removeSecurityGroup(eq(SERVER_ID), eq(SECURITY_GROUP_ID));
        verify(serverMock).getName();
        verify(serverMock).getId();
        verify(securityGroupMock).getName();
        verify(securityGroupMock).getId();
    }

    @Test
    void testFailed() {
        // given
        when(serverServiceMock.removeSecurityGroup(anyString(), anyString())).thenReturn(
                ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(() ->
                openstackNovaService.removeSecurityGroupFromInstance(serverMock, securityGroupMock, clientV3Mock));

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(String.format("Failed to remove SecurityGroup %s from server %s. Reason: %s",
                        SECURITY_GROUP_NAME, SERVER_NAME, EXCEPTION_MESSAGE));
        assertThat(((OpenstackException) throwable).getExceptionCode()).isEqualTo(
                REMOVE_SECURITY_GROUP_FROM_INSTANCE_FAILED);
        verify(serverServiceMock).removeSecurityGroup(eq(SERVER_ID), eq(SECURITY_GROUP_ID));
        verify(serverMock, times(2)).getName();
        verify(serverMock).getId();
        verify(securityGroupMock, times(2)).getName();
        verify(securityGroupMock).getId();
    }
}

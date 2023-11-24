package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.model.common.ActionResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.ADD_SECURITY_GROUP_FAILED;
import static org.mockito.Mockito.*;

public class AddSecurityGroupTest extends OpenstackNovaBaseTest {
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
        when(serverServiceMock.addSecurityGroup(anyString(), anyString())).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.addSecurityGroupToInstance(serverMock, securityGroupMock, clientV3Mock);

        // then
        verify(serverServiceMock).addSecurityGroup(SERVER_ID, SECURITY_GROUP_ID);
        verify(serverMock).getName();
        verify(serverMock).getId();
        verify(securityGroupMock).getName();
        verify(securityGroupMock).getId();
    }

    @Test
    void testFailed() {
        // given
        when(serverServiceMock.addSecurityGroup(anyString(), anyString())).thenReturn(
                ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(() ->
                openstackNovaService.addSecurityGroupToInstance(serverMock, securityGroupMock, clientV3Mock));

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(String.format("Failed to add SecurityGroup %s to server %s. Reason: %s",
                        SECURITY_GROUP_NAME, SERVER_NAME, EXCEPTION_MESSAGE));
        assertThat(((OpenstackException) throwable).getExceptionCode()).isEqualTo(ADD_SECURITY_GROUP_FAILED);
        verify(serverServiceMock).addSecurityGroup(SERVER_ID, SECURITY_GROUP_ID);
        verify(serverMock, times(2)).getName();
        verify(serverMock).getId();
        verify(securityGroupMock, times(2)).getName();
        verify(securityGroupMock).getId();
    }
}

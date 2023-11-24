package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.model.common.ActionResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveSecurityGroupTest extends OpenstackNeutronBaseTest {

    @Override
    protected void setUp() {
        when(networkingServiceMock.securitygroup()).thenReturn(securityGroupServiceMock);
        when(securityGroupMock.getId()).thenReturn(SECURITY_GROUP_ID);
        when(securityGroupServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());
    }

    @Test
    void testSuccess() throws OpenstackException {
        // when
        openstackNeutronService.removeSecurityGroup(securityGroupMock, clientV3Mock);

        // then
        verify(securityGroupServiceMock).delete(SECURITY_GROUP_ID);
    }

}

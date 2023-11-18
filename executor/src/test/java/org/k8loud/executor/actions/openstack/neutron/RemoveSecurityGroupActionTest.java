package org.k8loud.executor.actions.openstack.neutron;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.actions.openstack.RemoveSecurityGroupAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;

import static org.mockito.Mockito.*;

public class RemoveSecurityGroupActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "securityGroupId", SECURITY_GROUP_ID));

    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        RemoveSecurityGroupAction removeSecurityGroupAction = new RemoveSecurityGroupAction(
                VALID_PARAMS, openstackServiceMock);
        when(openstackServiceMock.removeSecurityGroup(anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = removeSecurityGroupAction.execute();

        // then
        verify(openstackServiceMock).removeSecurityGroup(eq(REGION), eq(SECURITY_GROUP_ID));
        assertSuccessResponse(response);
    }
}

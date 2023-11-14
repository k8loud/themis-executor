package org.k8loud.executor.actions.openstack.neutron;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.actions.openstack.AddRuleToSecurityGroupAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

import static org.mockito.Mockito.*;

public class AddRuleToSecurityGroupActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "securityGroupId", SECURITY_GROUP_ID, "ethertype", ETHERTYPE, "direction",
                    DIRECTION, "remoteIpPrefix", REMOTE_IP_PREFIX, "protocol", PROTOCOL, "portRangeMin", PORT_RANGE_MIN,
                    "portRangeMax", PORT_RANGE_MAX));

    @Test
    void testSuccess() throws ActionException, OpenstackException, ValidationException {
        // given
        AddRuleToSecurityGroupAction addRuleToSecurityGroupAction = new AddRuleToSecurityGroupAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.addRuleToSecurityGroup(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyInt(), nullable(String.class))).thenReturn(resultMap);

        // when
        ExecutionRS response = addRuleToSecurityGroupAction.execute();

        // then
        verify(openstackServiceMock).addRuleToSecurityGroup(eq(REGION), eq(SECURITY_GROUP_ID), eq(ETHERTYPE),
                eq(DIRECTION), eq(REMOTE_IP_PREFIX), eq(PROTOCOL), eq(Integer.parseInt(PORT_RANGE_MIN)),
                eq(Integer.parseInt(PORT_RANGE_MAX)), isNull(String.class));
        assertSuccessResponse(response);
    }
}

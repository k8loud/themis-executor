package org.k8loud.executor.action.openstack.neutron;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.AddRuleToSecurityGroupAction;
import org.k8loud.executor.action.openstack.CreateSecurityGroupAction;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

public class AddRuleToSecurityGroupActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "securityGroupId", SECURITY_GROUP_ID, "ethertype", ETHERTYPE, "direction",
                    DIRECTION, "remoteIpPrefix", REMOTE_IP_PREFIX, "protocol", PROTOCOL, "portRangeMin", PORT_RANGE_MIN,
                    "portRangeMax", PORT_RANGE_MAX));

    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        AddRuleToSecurityGroupAction addRuleToSecurityGroupAction = new AddRuleToSecurityGroupAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.addRuleToSecurityGroup(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyInt())).thenReturn(RESULT);

        // when
        ExecutionRS response = addRuleToSecurityGroupAction.execute();

        // then
        verify(openstackServiceMock).addRuleToSecurityGroup(REGION, SECURITY_GROUP_ID, ETHERTYPE, DIRECTION,
                REMOTE_IP_PREFIX, PROTOCOL, Integer.parseInt(PORT_RANGE_MIN), Integer.parseInt(PORT_RANGE_MAX));
        assertSuccessResponse(response);
    }
}

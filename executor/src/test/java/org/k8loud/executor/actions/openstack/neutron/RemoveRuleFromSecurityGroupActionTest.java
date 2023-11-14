package org.k8loud.executor.actions.openstack.neutron;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.actions.openstack.RemoveRuleFromSecurityGroupAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;

import static org.mockito.Mockito.*;

public class RemoveRuleFromSecurityGroupActionTest extends OpenstackActionBaseTest {
    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "securityGroupRuleId", SECURITY_GROUP_RULE_ID));

    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        RemoveRuleFromSecurityGroupAction removeRuleFromSecurityGroupAction = new RemoveRuleFromSecurityGroupAction(
                VALID_PARAMS, openstackServiceMock);
        when(openstackServiceMock.removeSecurityGroupRule(anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = removeRuleFromSecurityGroupAction.execute();

        // then
        verify(openstackServiceMock).removeSecurityGroupRule(eq(REGION), eq(SECURITY_GROUP_RULE_ID));
        assertSuccessResponse(response);
    }
}

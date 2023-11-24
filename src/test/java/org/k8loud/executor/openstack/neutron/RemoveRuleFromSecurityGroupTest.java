package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RemoveRuleFromSecurityGroupTest extends OpenstackNeutronBaseTest {

    @Override
    protected void setUp() {
        when(networkingServiceMock.securityrule()).thenReturn(securityGroupRuleServiceMock);
        when(securityGroupRuleMock.getId()).thenReturn(RULE_ID);
    }

    @Test
    void testSuccess() {
        // when
        openstackNeutronService.removeSecurityGroupRule(securityGroupRuleMock, clientV3Mock);

        // then
        verify(securityGroupRuleServiceMock).delete(RULE_ID);
    }

}

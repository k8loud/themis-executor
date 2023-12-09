package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.Util;
import org.openstack4j.api.Builders;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ModifySecurityGroupsDuringThrottlingTest extends OpenstackNeutronBaseTest {
    private static final String SUBNET_TO_EXCLUDE = "192.168.1.160/27";


    @Override
    protected void setUp() {
        when(networkingServiceMock.securityrule()).thenReturn(securityGroupRuleServiceMock);

        when(securityGroupRuleMock.getEtherType()).thenReturn(ETHERTYPE);
        when(securityGroupRuleMock.getRemoteIpPrefix()).thenReturn("192.168.1.0/24");
        when(securityGroupRuleMock.getPortRangeMin()).thenReturn(PORT_RANGE_MIN);
        when(securityGroupRuleMock.getPortRangeMax()).thenReturn(PORT_RANGE_MAX);
        when(securityGroupRuleMock.getDirection()).thenReturn("ingress");
        when(securityGroupRuleMock.getProtocol()).thenReturn(PROTOCOL);
        when(securityGroupRuleMock.getId()).thenReturn(RULE_ID);

        when(securityGroupRuleServiceMock.create(any(SecurityGroupRule.class))).thenAnswer(invocation ->
                randomIdSecurityGroupRule());
    }

    private static SecurityGroupRule randomIdSecurityGroupRule() {
        return Builders.securityGroupRule().securityGroupId(Util.nameWithUuid("test")).build();
    }

    @Test
    void testSuccess() throws OpenstackException {
        // given
        Map<SecurityGroup, Set<SecurityGroupRule>> rulesToModify = Map.of(securityGroupMock,
                Set.of(securityGroupRuleMock));

        // when
        Set<SecurityGroupRule> throttlingRules = openstackNeutronService.modifySecurityGroupsDuringThrottling(
                rulesToModify, SUBNET_TO_EXCLUDE, PORT_RANGE_MIN, PORT_RANGE_MAX, () -> clientV3Mock);

        // then
        // ["192.168.1.0/25", "192.168.1.128/27", "192.168.1.192/26"]
        verify(securityGroupRuleServiceMock, times(3)).create(any(SecurityGroupRule.class));
        assertThat(throttlingRules).hasSize(3);

        verify(securityGroupRuleServiceMock).delete(eq(RULE_ID));
    }
}

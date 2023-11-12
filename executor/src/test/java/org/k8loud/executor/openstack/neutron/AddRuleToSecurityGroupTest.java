package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.Builders;
import org.openstack4j.api.networking.SecurityGroupRuleService;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.ADD_RULE_FAILED;
import static org.mockito.Mockito.*;

public class AddRuleToSecurityGroupTest extends OpenstackNeutronBaseTest {
    private static final SecurityGroupRule SECURITY_GROUP_RULE = Builders.securityGroupRule()
            .securityGroupId(SECURITY_GROUP_ID)
            .direction(DIRECTION)
            .ethertype(ETHERTYPE)
            .portRangeMin(PORT_RANGE_MIN)
            .portRangeMax(PORT_RANGE_MAX)
            .protocol(PROTOCOL)
            .remoteIpPrefix(REMOTE_IP_PREFIX)
            .description(RULE_DESCRIPTION)
            .build();

    @Override
    protected void setUp() {
        when(networkingServiceMock.securityrule()).thenReturn(securityGroupRuleService);
        when(securityGroupMock.getId()).thenReturn(SECURITY_GROUP_ID);
    }

    @Test
    void testSuccess() throws OpenstackException {
        // given
        when(securityGroupRuleService.create(any(SecurityGroupRule.class))).thenReturn(securityGroupRuleMock);

        // when
        openstackNeutronService.addSecurityGroupRule(securityGroupMock, ETHERTYPE, DIRECTION, REMOTE_IP_PREFIX,
                PROTOCOL, PORT_RANGE_MIN, PORT_RANGE_MAX, RULE_DESCRIPTION, clientV3Mock);

        // then
        verify(securityGroupRuleService).create(refEq(SECURITY_GROUP_RULE));
        verify(securityGroupMock).getName();
    }

    @Test
    void testCreateSecurityGroupFailed() {
        // given
        when(securityGroupRuleService.create(any(SecurityGroupRule.class))).thenReturn(null);
        when(securityGroupMock.getName()).thenReturn(SECURITY_GROUP_NAME);


        // when
        Throwable throwable = catchThrowable(
                () -> openstackNeutronService.addSecurityGroupRule(securityGroupMock, ETHERTYPE, DIRECTION,
                        REMOTE_IP_PREFIX, PROTOCOL, PORT_RANGE_MIN, PORT_RANGE_MAX, RULE_DESCRIPTION, clientV3Mock));

        // then
        verify(securityGroupRuleService).create(refEq(SECURITY_GROUP_RULE));
        verify(securityGroupMock, times(2)).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Failed to add rule to SecurityGroup '%s'", SECURITY_GROUP_NAME);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(ADD_RULE_FAILED);
    }
}

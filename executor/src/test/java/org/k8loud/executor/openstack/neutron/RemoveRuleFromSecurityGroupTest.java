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

public class RemoveRuleFromSecurityGroupTest extends OpenstackNeutronBaseTest {

    @Override
    protected void setUp() {
        when(networkingServiceMock.securityrule()).thenReturn(securityGroupRuleService);
        when(securityGroupRuleMock.getId()).thenReturn(RULE_ID);
    }

    @Test
    void testSuccess() {
        // when
        openstackNeutronService.removeSecurityGroupRule(securityGroupRuleMock, clientV3Mock);

        // then
        verify(securityGroupRuleService).delete(RULE_ID);
    }

}

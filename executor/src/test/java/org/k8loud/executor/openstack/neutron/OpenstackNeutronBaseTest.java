package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.openstack.OpenstackConstants;
import org.k8loud.executor.openstack.OpenstackNeutronService;
import org.k8loud.executor.openstack.OpenstackNeutronServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.networking.NetworkingService;
import org.openstack4j.api.networking.SecurityGroupRuleService;
import org.openstack4j.api.networking.SecurityGroupService;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class OpenstackNeutronBaseTest extends OpenstackConstants {
    @Mock
    protected OSClient.OSClientV3 clientV3Mock;
    @Mock
    protected NetworkingService networkingServiceMock;
    @Mock
    protected SecurityGroupRuleService securityGroupRuleService;
    @Mock
    protected SecurityGroupService securityGroupServiceMock;
    @Mock
    protected SecurityGroupRule securityGroupRuleMock;
    @Mock
    protected SecurityGroup securityGroupMock;

    protected OpenstackNeutronService openstackNeutronService = new OpenstackNeutronServiceImpl();

    @BeforeEach
    protected void baseSetUp() {
        when(clientV3Mock.networking()).thenReturn(networkingServiceMock);
        setUp();
    }

    protected abstract void setUp();
}

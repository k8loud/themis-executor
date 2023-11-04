package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.BeforeEach;
import org.k8loud.executor.openstack.OpenstackConstants;
import org.k8loud.executor.openstack.OpenstackNeutronService;
import org.k8loud.executor.openstack.OpenstackNeutronServiceImpl;
import org.mockito.Mock;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.networking.NetworkingService;

import static org.mockito.Mockito.when;

public abstract class OpenstackNeutronBaseTest extends OpenstackConstants {
    @Mock
    protected OSClient.OSClientV3 clientV3Mock;
    @Mock
    protected NetworkingService networkingServiceMock;

    protected OpenstackNeutronService openstackNeutronService = new OpenstackNeutronServiceImpl();

    @BeforeEach
    protected void baseSetUp() {
        when(clientV3Mock.networking()).thenReturn(networkingServiceMock);
        setUp();
    }

    protected abstract void setUp();
}

package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.BeforeEach;
import org.k8loud.executor.openstack.OpenstackConstants;
import org.k8loud.executor.openstack.OpenstackNovaService;
import org.k8loud.executor.openstack.OpenstackNovaServiceImpl;
import org.mockito.Mock;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.compute.Server;

import static org.mockito.Mockito.when;

public abstract class OpenstackNovaBaseTest extends OpenstackConstants {
    @Mock
    protected OSClient.OSClientV3 clientV3Mock;
    @Mock
    protected Server server;
    @Mock
    protected ComputeService computeService;
    @Mock
    protected ServerService serverService;
    protected OpenstackNovaService openstackNovaService = new OpenstackNovaServiceImpl();

    @BeforeEach
    protected void baseSetUp() {
        when(clientV3Mock.compute()).thenReturn(computeService);
        when(computeService.servers()).thenReturn(serverService);
        setUp();
    }

    protected abstract void setUp();
}

package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.openstack.OpenstackNovaService;
import org.k8loud.executor.openstack.OpenstackNovaServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.ACTIVE;

@ExtendWith(MockitoExtension.class)
public class CreateServerTest {
    private static final String SERVER_NAME = "new-server-name";
    private static final String FLAVOR_ID = "flavorId";
    private static final String SERVER_ID = "serverId";
    private static final int WAIT_FOR_ACTIVE_SEC = 100;
    private static final int WAIT_FOR_ACTIVE_MILLIS = WAIT_FOR_ACTIVE_SEC * 1000;


    private static final ServerCreate SERVER_CREATE = Builders.server()
            .name(SERVER_NAME)
            .flavor(FLAVOR_ID)
            .image(SERVER_ID)
            .keypairName("default-from-api")
            .build();

    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    ComputeService computeService;
    @Mock
    ServerService serverService;
    OpenstackNovaService openstackNovaService = new OpenstackNovaServiceImpl();

    @BeforeEach
    public void setup() {
        when(clientV3Mock.compute()).thenReturn(computeService);
        when(computeService.servers()).thenReturn(serverService);

        when(serverService.bootAndWaitActive(any(ServerCreate.class), anyInt())).thenReturn(server);

        when(server.getStatus()).thenReturn(ACTIVE);
    }

    @Test
    void testCreateServerWithoutOptionalSetup() {
        // when
        openstackNovaService.createServer(SERVER_NAME, FLAVOR_ID, SERVER_ID, WAIT_FOR_ACTIVE_SEC, clientV3Mock);

        // then
        verify(serverService).bootAndWaitActive(refEq(SERVER_CREATE), eq(WAIT_FOR_ACTIVE_MILLIS));
    }

    @Test
    void testCreateServerWithOptionalSetup() {
        // when
        openstackNovaService.createServer(SERVER_NAME, FLAVOR_ID, SERVER_ID, WAIT_FOR_ACTIVE_SEC, clientV3Mock,
                b -> b.addAdminPass("1234").build());

        // then
        verify(serverService).bootAndWaitActive(not(refEq(SERVER_CREATE)), eq(WAIT_FOR_ACTIVE_MILLIS));
    }
}

package org.k8loud.executor.openstack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.VERIFY_RESIZE;

@ExtendWith(MockitoExtension.class)
public class ResizeServerUpTest {
    private static final String REGION = "region";
    private static final String NEW_FLAVOR_ID = "flavorId";
    private static final String SERVER_ID = "serverId";
    private static final int CURRENT_FLAVOR_VCPUS = 4;

    @Mock
    OpenstackClientProvider openstackClientProviderMock;
    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    Flavor currentFlavor;
    @Mock
    Flavor newFlavor;
    @Mock
    OpenstackNovaService openstackNovaServiceMock;

    OpenstackService openstackService;


    @BeforeEach
    void setUp() {
        openstackService = new OpenstackServiceImpl(openstackClientProviderMock, openstackNovaServiceMock, null);
    }

    @Test
    void testResizeServerSuccess() throws OpenstackException {
        // given
        setUpMocks();
        when(newFlavor.getVcpus()).thenReturn(CURRENT_FLAVOR_VCPUS + 1);

        // when
        openstackService.resizeServerUp(REGION, SERVER_ID, NEW_FLAVOR_ID);

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackNovaServiceMock).getFlavor(eq(NEW_FLAVOR_ID), eq(clientV3Mock));
        verify(openstackNovaServiceMock).resize(eq(server), eq(newFlavor), eq(clientV3Mock));
        verify(openstackNovaServiceMock).confirmResize(eq(server), eq(clientV3Mock));
        verify(openstackNovaServiceMock).waitForServerStatus(eq(server), eq(VERIFY_RESIZE), anyInt(), eq(clientV3Mock));
    }

    private void setUpMocks() throws OpenstackException {
        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);

        when(openstackNovaServiceMock.getServer(anyString(), any(OSClient.OSClientV3.class))).thenReturn(server);
        when(openstackNovaServiceMock.getFlavor(anyString(), any(OSClient.OSClientV3.class))).thenReturn(newFlavor);

        when(server.getFlavor()).thenReturn(currentFlavor);
        when(currentFlavor.getVcpus()).thenReturn(CURRENT_FLAVOR_VCPUS);
    }
}

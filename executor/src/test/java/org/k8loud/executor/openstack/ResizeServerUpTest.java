package org.k8loud.executor.openstack;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.VERIFY_RESIZE;

public class ResizeServerUpTest extends OpenstackBaseTest {
    private static final String NEW_FLAVOR_ID = "flavorId";
    private static final int CURRENT_FLAVOR_VCPUS = 4;

    @Mock
    Flavor currentFlavorMock;
    @Mock
    Flavor newFlavorMock;

    @Override
    protected void setUp() throws OpenstackException {
        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);

        when(serverMock.getId()).thenReturn(SERVER_ID);

        when(openstackNovaServiceMock.getServer(anyString(), any(OSClient.OSClientV3.class))).thenReturn(serverMock);
        when(openstackNovaServiceMock.getFlavor(anyString(), any(OSClient.OSClientV3.class))).thenReturn(newFlavorMock);

        when(serverMock.getFlavor()).thenReturn(currentFlavorMock);
        when(currentFlavorMock.getVcpus()).thenReturn(CURRENT_FLAVOR_VCPUS);
    }

    @Test
    void testResizeServerSuccess() throws OpenstackException {
        // given
        when(newFlavorMock.getVcpus()).thenReturn(CURRENT_FLAVOR_VCPUS + 1);

        // when
        Map<String, String> res = openstackService.resizeServerUp(REGION, SERVER_ID, NEW_FLAVOR_ID);

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackNovaServiceMock).getFlavor(eq(NEW_FLAVOR_ID), eq(clientV3Mock));
        verify(openstackNovaServiceMock).resize(eq(serverMock), eq(newFlavorMock), eq(clientV3Mock));
        verify(openstackNovaServiceMock).confirmResize(eq(serverMock), eq(clientV3Mock));
        verify(openstackNovaServiceMock).waitForServerStatus(eq(serverMock), eq(VERIFY_RESIZE), anyInt(), eq(clientV3Mock));
        assertResult(String.format("Resizing a server with id=%s finished with success", SERVER_ID), res);
    }
}

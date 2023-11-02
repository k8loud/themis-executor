package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.Builders;
import org.openstack4j.model.compute.ServerCreate;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.ACTIVE;

@ExtendWith(MockitoExtension.class)
public class CreateServerTest extends OpenstackNovaBaseTest {
    private static final String FLAVOR_ID = "flavorId";
    private static final int WAIT_FOR_ACTIVE_SEC = 100;
    private static final int WAIT_FOR_ACTIVE_MILLIS = WAIT_FOR_ACTIVE_SEC * 1000;

    private static final ServerCreate SERVER_CREATE = Builders.server()
            .name(SERVER_NAME)
            .flavor(FLAVOR_ID)
            .image(SERVER_ID)
            .keypairName("default")
            .build();

    @Override
    public void setUp() {
        when(serverServiceMock.bootAndWaitActive(any(ServerCreate.class), anyInt())).thenReturn(serverMock);
        when(serverMock.getStatus()).thenReturn(ACTIVE);
    }

    @Test
    void testCreateServerWithoutOptionalSetup() {
        // when
        openstackNovaService.createServer(SERVER_NAME, FLAVOR_ID, SERVER_ID, WAIT_FOR_ACTIVE_SEC, clientV3Mock);

        // then
        verify(serverServiceMock).bootAndWaitActive(refEq(SERVER_CREATE), eq(WAIT_FOR_ACTIVE_MILLIS));
    }

    @Test
    void testCreateServerWithOptionalSetup() {
        // when
        openstackNovaService.createServer(SERVER_NAME, FLAVOR_ID, SERVER_ID, WAIT_FOR_ACTIVE_SEC, clientV3Mock,
                b -> b.addAdminPass("1234").build());

        // then
        verify(serverServiceMock).bootAndWaitActive(not(refEq(SERVER_CREATE)), eq(WAIT_FOR_ACTIVE_MILLIS));
    }
}

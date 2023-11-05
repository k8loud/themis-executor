package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.Builders;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.image.v2.Image;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.ACTIVE;

@ExtendWith(MockitoExtension.class)
public class CreateServersTest extends OpenstackNovaBaseTest {
    @Mock
    Flavor flavorMock;
    @Mock
    Image imageMock;
    private static final String FLAVOR_ID = "flavorId";
    private static final String KEYPAIR = "default";
    private static final String USER_DATA = "script";
    public static final int WAIT_ACTIVE = 150;

    @Override
    public void setUp() {
        when(serverServiceMock.bootAndWaitActive(any(ServerCreate.class), anyInt())).thenReturn(serverMock);
        when(serverMock.getName()).thenReturn(SERVER_NAME + 1);
        when(imageMock.getId()).thenReturn(IMAGE_ID);
        when(flavorMock.getId()).thenReturn(FLAVOR_ID);
    }

    @Test
    void testCreateServer() throws OpenstackException {
        // given
        ArgumentCaptor<ServerCreate> captor = ArgumentCaptor.forClass(ServerCreate.class);
        // when
        openstackNovaService.createServers(SERVER_NAME, imageMock, flavorMock, KEYPAIR, SECURITY_GROUP_NAME, USER_DATA,
                1, WAIT_ACTIVE, () -> clientV3Mock);

        // then
        verify(serverServiceMock).bootAndWaitActive(captor.capture(), eq(WAIT_ACTIVE * 1000));

        ServerCreate captured = captor.getValue();
        assertThat(captured.getName())
                .startsWith(SERVER_NAME);
        assertThat(captured.getSecurityGroups())
                .extracting(SecurityGroup::getName)
                .containsExactly(SECURITY_GROUP_NAME);
        assertThat(captured)
                .extracting("flavorRef", "imageRef", "keyName", "userData")
                .containsExactly(FLAVOR_ID, IMAGE_ID, KEYPAIR, Base64.getEncoder().encodeToString(USER_DATA.getBytes()));

    }
}

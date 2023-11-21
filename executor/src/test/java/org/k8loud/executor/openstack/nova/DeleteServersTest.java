package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.Util;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.image.v2.Image;

import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DeleteServersTest extends OpenstackNovaBaseTest {
    public static final Pattern PATTERN_NAME = Pattern.compile(SERVER_NAME + "-.{8}");
    @Override
    public void setUp() {
        when(serverServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());
    }

    @Test
    void testCreateServer() throws OpenstackException {
        // given
        List<Server> servers = List.of(createServerMock(), createDifferentServerMock(), createServerMock());
        when(serverServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());
        doReturn(servers).when(serverServiceMock).list();

        // when
        openstackNovaService.deleteServers(PATTERN_NAME, () -> clientV3Mock);

        // then
        verify(serverServiceMock, times(2)).delete(SERVER_ID);
    }

    private Server createServerMock(){
        Server server = mock(Server.class);
        when(server.getName()).thenReturn(Util.nameWithUuid(SERVER_NAME));
        when(server.getId()).thenReturn(SERVER_ID);
        return server;
    }

    private Server createDifferentServerMock(){
        Server server = mock(Server.class);
        when(server.getName()).thenReturn("DifferentServerName");
        return server;
    }
}

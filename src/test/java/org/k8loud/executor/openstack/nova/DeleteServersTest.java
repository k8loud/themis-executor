package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.Util;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;

import java.util.List;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;

public class DeleteServersTest extends OpenstackNovaBaseTest {
    public static final Pattern PATTERN_NAME = Pattern.compile(SERVER_NAME + "-.{8}");
    @Override
    public void setUp() {
        when(serverServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());
    }

    @Test
    void testDeleteServerWithPatternName() throws OpenstackException {
        // given
        List<Server> servers = List.of(createServerMock(), createDifferentServerMock(), createServerMock());
        when(serverServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());
        doReturn(servers).when(serverServiceMock).list();

        // when
        openstackNovaService.deleteServers(PATTERN_NAME, () -> clientV3Mock);

        // then
        verify(serverServiceMock, times(2)).delete(SERVER_ID);
        verifyNoMoreInteractions(serverServiceMock);
    }

    @Test
    void testDeleteServerWithServerList() throws OpenstackException {
        // given
        Server server1 = createServerMock("id1");
        Server server2 = createServerMock("id2");
        List<Server> servers = List.of(server1, server2);
        when(serverServiceMock.delete(anyString())).thenReturn(ActionResponse.actionSuccess());

        // when
        openstackNovaService.deleteServers(servers, () -> clientV3Mock);

        // then
        verify(serverServiceMock).delete("id1");
        verify(serverServiceMock).delete("id2");
        verifyNoMoreInteractions(serverServiceMock);
    }

    private Server createServerMock() {
        return createServerMock(SERVER_ID);
    }

    private Server createServerMock(String serverId) {
        Server server = mock(Server.class);
        when(server.getName()).thenReturn(Util.nameWithUuid(SERVER_NAME));
        when(server.getId()).thenReturn(serverId);
        return server;
    }

    private Server createDifferentServerMock() {
        Server server = mock(Server.class);
        when(server.getName()).thenReturn("DifferentServerName");
        return server;
    }
}

package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackNovaService;
import org.k8loud.executor.openstack.OpenstackNovaServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ComputeService;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.compute.Server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.CREATE_SERVER_SNAPSHOT_FAILED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateServerSnapshotTest {
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_ID = "serverId";
    private static final String SNAPSHOT_NAME = "snapshotName";
    private static final String SNAPSHOT_ID = "snapshotId";

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
        when(server.getName()).thenReturn(SERVER_NAME);
        when(server.getId()).thenReturn(SERVER_ID);
    }

    @Test
    void testCreateServerSnapshotSuccess() throws OpenstackException {
        // given
        when(serverService.createSnapshot(anyString(), anyString())).thenReturn(SNAPSHOT_ID);

        // when
        openstackNovaService.createServerSnapshot(server, SNAPSHOT_NAME, clientV3Mock);

        // then
        verify(serverService).createSnapshot(SERVER_ID, SNAPSHOT_NAME);
        verify(server).getName();
        verify(server).getId();
    }

    @Test
    void testCreateServerSnapshotFailed() {
        // given
        when(serverService.createSnapshot(anyString(), anyString())).thenReturn(null);

        // when
        Throwable throwable = catchThrowable(() ->
                openstackNovaService.createServerSnapshot(server, SNAPSHOT_NAME, clientV3Mock));

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(String.format("Failed to create snapshot with name \"%s\" on server %s",
                        SNAPSHOT_NAME, SERVER_NAME));
        assertThat(((OpenstackException) throwable).getExceptionCode()).isEqualTo(CREATE_SERVER_SNAPSHOT_FAILED);
        verify(serverService).createSnapshot(SERVER_ID, SNAPSHOT_NAME);
        verify(server, times(3)).getName();
        verify(server).getId();
    }
}

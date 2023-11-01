package org.k8loud.executor.openstack.nova;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.CREATE_SERVER_SNAPSHOT_FAILED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateServerSnapshotTest extends OpenstackNovaBaseTest {
    private static final String SNAPSHOT_ID = "snapshotId";

    @Override
    public void setUp() {
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

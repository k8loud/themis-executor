package org.k8loud.executor.openstack.cinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackCinderService;
import org.k8loud.executor.openstack.OpenstackCinderServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.BlockStorageService;
import org.openstack4j.api.storage.BlockVolumeSnapshotService;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.CREATE_VOLUME_SNAPSHOT_FAILED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateVolumeSnapshotTest {
    private static final String VOLUME_ID = "volumeId";
    private static final String VOLUME_NAME = "volumeName";
    private static final String SNAPSHOT_NAME = "snapshotName";

    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Volume volumeMock;
    @Mock
    VolumeSnapshot volumeSnapshotMock;
    @Mock
    BlockStorageService blockStorageServiceMock;
    @Mock
    BlockVolumeSnapshotService blockVolumeSnapshotServiceMock;

    OpenstackCinderService openstackCinderService;
    ArgumentCaptor<VolumeSnapshot> volumeSnapshotArgumentCaptor;
    @BeforeEach
    public void setup() {
        openstackCinderService = new OpenstackCinderServiceImpl();

        when(volumeMock.getName()).thenReturn(VOLUME_NAME);
        when(volumeMock.getId()).thenReturn(VOLUME_ID);

        when(clientV3Mock.blockStorage()).thenReturn(blockStorageServiceMock);
        when(blockStorageServiceMock.snapshots()).thenReturn(blockVolumeSnapshotServiceMock);
        volumeSnapshotArgumentCaptor = ArgumentCaptor.forClass(VolumeSnapshot.class);

    }

    @Test
    void testSuccess() throws OpenstackException {
        // given
        when(blockVolumeSnapshotServiceMock.create(any(VolumeSnapshot.class)))
                .thenReturn(volumeSnapshotMock);

        // when
        openstackCinderService.createVolumeSnapshot(volumeMock, SNAPSHOT_NAME, clientV3Mock);

        // then
        verify(blockVolumeSnapshotServiceMock).create(volumeSnapshotArgumentCaptor.capture());
        verify(volumeMock).getName();
        verify(volumeMock).getId();
    }

    @Test
    void testFailed() {
        // given
        when(blockVolumeSnapshotServiceMock.create(any(VolumeSnapshot.class))).thenReturn(null);

        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.createVolumeSnapshot(volumeMock, SNAPSHOT_NAME, clientV3Mock));

        // then
        verify(volumeMock, times(3)).getName();
        verify(volumeMock).getId();
        verify(blockVolumeSnapshotServiceMock).create(volumeSnapshotArgumentCaptor.capture());

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Failed to create snapshot %s of a volume %s", SNAPSHOT_NAME, VOLUME_NAME);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(CREATE_VOLUME_SNAPSHOT_FAILED);
    }
}

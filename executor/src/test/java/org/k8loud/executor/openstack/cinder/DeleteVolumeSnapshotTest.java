package org.k8loud.executor.openstack.cinder;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackCinderServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.storage.BlockVolumeSnapshotService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.storage.block.VolumeSnapshot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.DELETE_VOLUME_SNAPSHOT_FAILED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteVolumeSnapshotTest extends OpenstackCinderBaseTest {
    private static final String OLDEST_SNAPSHOT_NAME = "oldest";
    private static final String OLDEST_SNAPSHOT_ID = "oldestId";

    @Mock
    BlockVolumeSnapshotService blockVolumeSnapshotServiceMock;

    ArgumentCaptor<VolumeSnapshot> volumeSnapshotArgumentCaptor;

    @Override
    public void setUp() {
        openstackCinderService = new OpenstackCinderServiceImpl();

        when(volumeMock.getName()).thenReturn(VOLUME_NAME);
        when(volumeMock.getId()).thenReturn(VOLUME_ID);

        when(clientV3Mock.blockStorage()).thenReturn(blockStorageServiceMock);
        when(blockStorageServiceMock.snapshots()).thenReturn(blockVolumeSnapshotServiceMock);
        volumeSnapshotArgumentCaptor = ArgumentCaptor.forClass(VolumeSnapshot.class);

    }

    @ParameterizedTest
    @MethodSource("goodParameters")
    void testSuccess(List<VolumeSnapshot> snapshots, boolean keepOneSnapshot) throws OpenstackException {
        // given
        doReturn(snapshots).when(blockVolumeSnapshotServiceMock).list();
        doReturn(ActionResponse.actionSuccess()).when(blockVolumeSnapshotServiceMock).delete(anyString());

        // when
        openstackCinderService.deleteTheOldestVolumeSnapshot(volumeMock, keepOneSnapshot, clientV3Mock);

        // then
        verify(blockVolumeSnapshotServiceMock).list();
        verify(volumeMock).getName();
        verify(volumeMock, times(snapshots.size())).getId();
        verify(blockVolumeSnapshotServiceMock).delete(OLDEST_SNAPSHOT_ID);
    }

    @ParameterizedTest
    @MethodSource("goodParameters")
    void testResponseFailed(List<VolumeSnapshot> snapshots, boolean keepOneSnapshot) throws OpenstackException {
        // given
        doReturn(snapshots).when(blockVolumeSnapshotServiceMock).list();
        doReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123)).when(blockVolumeSnapshotServiceMock).delete(anyString());

        // when
        Throwable throwable = catchThrowable(() ->
                openstackCinderService.deleteTheOldestVolumeSnapshot(volumeMock, keepOneSnapshot, clientV3Mock));

        // then
        verify(blockVolumeSnapshotServiceMock).list();
        verify(volumeMock, times(2)).getName();
        verify(volumeMock, times(snapshots.size())).getId();
    }

    private static Stream<Arguments> goodParameters(){
        return Stream.of(
                // keepOne=false and one snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot(null, randomLowerDate()),
                                createMockSnapshot("somethingElse", randomLowerDate()),
                                createTheOldestSnapshot()),
                        false
                ),
                // keepOne=false and more than snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot("somethingElse", randomLowerDate()),
                                createMockSnapshot(null, randomLowerDate()),
                                createTheOldestSnapshot(),
                                createMockSnapshot(VOLUME_ID, randomBiggerDate()),
                                createMockSnapshot(VOLUME_ID, randomBiggerDate())),
                        false
                ),
                // keepOne=true and more than one than snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot("somethingElse", randomLowerDate()),
                                createMockSnapshot(null, randomLowerDate()),
                                createTheOldestSnapshot(),
                                createMockSnapshot(VOLUME_ID, randomBiggerDate()),
                                createMockSnapshot(VOLUME_ID, randomBiggerDate())),
                        true
                )
        );
    }

    @ParameterizedTest
    @MethodSource("badParameters")
    void testFailed(List<VolumeSnapshot> snapshots, boolean keepOneSnapshot, String errorMessage) {
        // given
        doReturn(snapshots).when(blockVolumeSnapshotServiceMock).list();

        // when
        Throwable throwable = catchThrowable(() ->
                openstackCinderService.deleteTheOldestVolumeSnapshot(volumeMock, keepOneSnapshot, clientV3Mock));

        // then
        verify(blockVolumeSnapshotServiceMock).list();
        verify(volumeMock, times(3)).getName();
        verify(volumeMock, times(snapshots.size())).getId();
        verify(blockVolumeSnapshotServiceMock, never()).delete(anyString());

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(errorMessage);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(DELETE_VOLUME_SNAPSHOT_FAILED);
    }

    private static Stream<Arguments> badParameters(){
        return Stream.of(
                // keepOne=true and one snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot("somethingElse", randomBiggerDate()),
                                createMockSnapshot(null, randomLowerDate()),
                                createTheOldestSnapshot()),
                        true,
                        String.format("Volume %s has 1 snapshot, and keepOneSnapshot was set on true", VOLUME_NAME)
                ),
                // keepOne=true and zero snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot("somethingElse", randomLowerDate()),
                                createMockSnapshot(null, randomLowerDate())),
                        true,
                        String.format("Volume %s does not have any snapshots", VOLUME_NAME)
                ),
                // keepOne=false and zero snapshots to delete
                Arguments.of(
                        List.of(
                                createMockSnapshot("somethingElse", randomLowerDate()),
                                createMockSnapshot(null, randomLowerDate())),
                        false,
                        String.format("Volume %s does not have any snapshots", VOLUME_NAME)
                )
        );
    }

    private static VolumeSnapshot createTheOldestSnapshot(){
        VolumeSnapshot snapshot = createMockSnapshot(VOLUME_ID, Date.from(Instant.now()));
        lenient().when(snapshot.getId()).thenReturn(OLDEST_SNAPSHOT_ID);
        lenient().when(snapshot.getName()).thenReturn(OLDEST_SNAPSHOT_NAME);
        return snapshot;
    }

    private static VolumeSnapshot createMockSnapshot(String volumeId, Date createdAt) {
        VolumeSnapshot snapshot = Mockito.mock(VolumeSnapshot.class);
        when(snapshot.getVolumeId()).thenReturn(volumeId);
        when(snapshot.getCreated()).thenReturn(createdAt);
        return snapshot;
    }

    private static Date randomBiggerDate(){
        return Date.from(Instant.now().plus(ThreadLocalRandom.current().nextInt(1, 11), ChronoUnit.DAYS));
    }

    private static Date randomLowerDate(){
        return Date.from(Instant.now().minus(ThreadLocalRandom.current().nextInt(1, 11), ChronoUnit.DAYS));
    }
}

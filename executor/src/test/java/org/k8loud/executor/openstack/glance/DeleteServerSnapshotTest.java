package org.k8loud.executor.openstack.glance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackGlanceService;
import org.k8loud.executor.openstack.OpenstackGlanceServiceImpl;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.api.image.v2.ImageService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.DELETE_SERVER_SNAPSHOT_FAILED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteServerSnapshotTest {
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_ID = "serverId";
    private static final String OLDEST_IMAGE_ID = "oldestImageId";
    private static final String OLDEST_IMAGE_NAME = "oldestImageName";
    private static final String EXCEPTION_MESSAGE = "Whatever message";
    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    Server serverId;
    @Mock
    ImageService imageServiceMock;
    @Mock
    List<Image> imageList;
    @Mock
    ServerService serverService;
    OpenstackGlanceService openstackGlanceService = new OpenstackGlanceServiceImpl();

    @BeforeEach
    void setUpMocks(){
        doReturn(imageServiceMock).when(clientV3Mock).imagesV2();
        when(server.getName()).thenReturn(SERVER_NAME);
        when(server.getId()).thenReturn(SERVER_ID);
    }

    @ParameterizedTest
    @MethodSource("goodParameters")
    void testDeleteServerSnapshotSuccess(List<Image> images, boolean keepOneSnapshot) throws OpenstackException {
        // given
        doReturn(images).when(imageServiceMock).list();
        doReturn(ActionResponse.actionSuccess()).when(imageServiceMock).delete(anyString());

        // when
        openstackGlanceService.deleteTheOldestSnapshot(server, keepOneSnapshot, clientV3Mock);

        // then
        verify(imageServiceMock).list();
        verify(imageServiceMock).delete(OLDEST_IMAGE_ID);
        verify(server).getName();
    }

    @ParameterizedTest
    @MethodSource("goodParameters")
    void testDeleteServerSnapshotFailed(List<Image> images, boolean keepOneSnapshot) {
        // given
        doReturn(images).when(imageServiceMock).list();
        doReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123)).when(imageServiceMock).delete(anyString());

        // when
        Throwable throwable = catchThrowable(() ->
                openstackGlanceService.deleteTheOldestSnapshot(server, keepOneSnapshot, clientV3Mock));

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isEqualTo(DELETE_SERVER_SNAPSHOT_FAILED);
        verify(imageServiceMock).list();
        verify(imageServiceMock).delete(OLDEST_IMAGE_ID);
        verify(server, times(2)).getName();
    }

    private static Stream<Arguments> goodParameters(){
        return Stream.of(
                // keepOne=false and one snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false),
                                createTheOldestImage()),
                        false
                ),
                // keepOne=false and more than snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false),
                                createTheOldestImage(),
                                createMockImage(SERVER_ID, randomBiggerDate(), false),
                                createMockImage(SERVER_ID, randomBiggerDate(), false)),
                        false
                ),
                // keepOne=true and more than one than snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false),
                                createTheOldestImage(),
                                createMockImage(SERVER_ID, randomBiggerDate(), false),
                                createMockImage(SERVER_ID, randomBiggerDate(), false)),
                        true
                )
        );
    }

    @ParameterizedTest
    @MethodSource("badParameters")
    void testDeleteServerSnapshotFailedSnapshotNotFound(List<Image> images, boolean keepOneSnapshot, String errorTemplate) {
        // given
        doReturn(images).when(imageServiceMock).list();

        // when
        Throwable throwable = catchThrowable(() ->
                openstackGlanceService.deleteTheOldestSnapshot(server, keepOneSnapshot, clientV3Mock));

        // then
        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(String.format(errorTemplate, SERVER_NAME));
        assertThat(((OpenstackException) throwable).getExceptionCode()).isEqualTo(DELETE_SERVER_SNAPSHOT_FAILED);
        verify(imageServiceMock).list();
        verify(imageServiceMock, never()).delete(OLDEST_IMAGE_ID);
        verify(server, times(3)).getName();
    }

    private static Stream<Arguments> badParameters(){
        return Stream.of(
                // keepOne=true and one snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false),
                                createTheOldestImage()),
                        true,
                        "Server %s has 1 snapshots, and keepOneSnapshot was set on true"
                ),
                // keepOne=true and zero snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false)),
                        true,
                        "Server %s does not have any snapshots"
                ),
                // keepOne=false and zero snapshots to delete
                Arguments.of(
                        List.of(
                                createMockImage(SERVER_ID, randomLowerDate(), true),
                                createMockImage(null, randomLowerDate(), false)),
                        false,
                        "Server %s does not have any snapshots"
                )
        );
    }

    private static Image createTheOldestImage(){
        Image image = createMockImage(SERVER_ID, Date.from(Instant.now()), false);
        lenient().when(image.getId()).thenReturn(OLDEST_IMAGE_ID);
        lenient().when(image.getName()).thenReturn(OLDEST_IMAGE_NAME);
        return image;
    }

    private static Image createMockImage(String uuid, Date createdAt, boolean isProtected) {
        Image image = Mockito.mock(Image.class);
        when(image.getInstanceUuid()).thenReturn(uuid);
        when(image.getUpdatedAt()).thenReturn(createdAt);
        when(image.getIsProtected()).thenReturn(isProtected);
        return image;
    }

    private static Date randomBiggerDate(){
        return Date.from(Instant.now().plus(ThreadLocalRandom.current().nextInt(1, 11), ChronoUnit.DAYS));
    }

    private static Date randomLowerDate(){
        return Date.from(Instant.now().minus(ThreadLocalRandom.current().nextInt(1, 11), ChronoUnit.DAYS));
    }
}

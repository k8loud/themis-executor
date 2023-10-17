package org.k8loud.executor.openstack.cinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackCinderService;
import org.k8loud.executor.openstack.OpenstackCinderServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.BlockStorageService;
import org.openstack4j.api.storage.BlockVolumeService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.DETACH_VOLUME_FAILED;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.VOLUME_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DetachVolumeTest {
    private static final String VOLUME_ID = "volumeId";
    private static final String VOLUME_NAME = "volumeName";
    private static final String SERVER_ID = "serverId";
    private static final String SERVER_NAME = "serverName";
    public static final String ATTACHMENT_ID = "attachmentId";
    public static final String EXCEPTION_MESSAGE = "Whatever message";

    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server serverMock;
    @Mock
    Volume volumeMock;
    @Mock
    BlockStorageService blockStorageServiceMock;
    @Mock
    BlockVolumeService blockVolumeServiceMock;
    @Mock
    VolumeAttachment volumeAttachmentMock;

    OpenstackCinderService openstackCinderService;

    @BeforeEach
    public void setup() {
        openstackCinderService = new OpenstackCinderServiceImpl();

        when(serverMock.getName()).thenReturn(SERVER_NAME);
        when(volumeMock.getName()).thenReturn(VOLUME_NAME);
    }

    @Test
    void testDetachVolume() throws OpenstackException {
        // given
        setUpMockForMethodExecution();
        when(blockVolumeServiceMock.detach(anyString(), anyString()))
                .thenReturn(ActionResponse.actionSuccess());
        // when
        openstackCinderService.detachVolume(serverMock, volumeMock, clientV3Mock);

        // then
        verify(volumeMock).getAttachments();
        verify(blockVolumeServiceMock).detach(VOLUME_ID, ATTACHMENT_ID);
        verify(volumeMock).getName();
        verify(serverMock).getName();
    }

    @Test
    void testDetachVolumeFailed() {
        // given
        setUpMockForMethodExecution();
        when(blockVolumeServiceMock.detach(anyString(), anyString()))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));

        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.detachVolume(serverMock, volumeMock, clientV3Mock));

        // then
        verify(volumeMock).getAttachments();
        verify(blockVolumeServiceMock).detach(VOLUME_ID, ATTACHMENT_ID);
        verify(volumeMock, times(2)).getName();
        verify(serverMock, times(2)).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(DETACH_VOLUME_FAILED);
    }

    @Test
    void testDetachVolumeFailedNoAttachmentFound() {
        // given
        doReturn(List.of()).when(volumeMock).getAttachments();

        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.detachVolume(serverMock, volumeMock, clientV3Mock));

        // then
        verify(volumeMock).getAttachments();
        verify(volumeMock, times(2)).getName();
        verify(serverMock, times(2)).getName();
        verifyNoInteractions(blockVolumeServiceMock);

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Volume %s has 0 attachments to server %s", VOLUME_NAME, SERVER_NAME);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(VOLUME_ERROR);
    }

    @Test
    void testDetachVolumeAttachmentToDifferentServer() {
        // given
        doReturn(List.of(volumeAttachmentMock)).when(volumeMock).getAttachments();
        when(serverMock.getId()).thenReturn(SERVER_ID);
        when(volumeAttachmentMock.getServerId()).thenReturn("DIFFERENT_ID");

        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.detachVolume(serverMock, volumeMock, clientV3Mock));

        // then
        verify(volumeMock).getAttachments();
        verify(volumeMock, times(2)).getName();
        verify(serverMock, times(2)).getName();
        verify(serverMock).getId();
        verifyNoInteractions(blockVolumeServiceMock);

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Volume %s has 0 attachments to server %s", VOLUME_NAME, SERVER_NAME);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(VOLUME_ERROR);
    }

    private void setUpMockForMethodExecution() {
        doReturn(List.of(volumeAttachmentMock)).when(volumeMock).getAttachments();
        when(serverMock.getId()).thenReturn(SERVER_ID);
        when(volumeAttachmentMock.getServerId()).thenReturn(SERVER_ID);
        when(volumeAttachmentMock.getAttachmentId()).thenReturn(ATTACHMENT_ID);
        when(clientV3Mock.blockStorage()).thenReturn(blockStorageServiceMock);
        when(blockStorageServiceMock.volumes()).thenReturn(blockVolumeServiceMock);
        when(volumeMock.getId()).thenReturn(VOLUME_ID);
    }
}

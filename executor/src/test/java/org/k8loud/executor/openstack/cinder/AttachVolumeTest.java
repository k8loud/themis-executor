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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.ATTACH_VOLUME_FAILED;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachVolumeTest {
    private static final String VOLUME_ID = "volumeId";
    private static final String VOLUME_NAME = "volumeName";
    private static final String SERVER_ID = "serverId";
    private static final String SERVER_NAME = "serverName";
    private static final String DEVICE = "/dev/test";
    public static final String SERVER_HOST_ID = "hostId";
    public static final String EXCEPTION_MESSAGE = "Whatever message";

    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    Volume volume;
    @Mock
    BlockStorageService blockStorageService;
    @Mock
    BlockVolumeService blockVolumeService;

    OpenstackCinderService openstackCinderService;

    @BeforeEach
    public void setup() {
        openstackCinderService = new OpenstackCinderServiceImpl();

        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getHostId()).thenReturn(SERVER_HOST_ID);
        when(volume.getId()).thenReturn(VOLUME_ID);

        when(clientV3Mock.blockStorage()).thenReturn(blockStorageService);
        when(blockStorageService.volumes()).thenReturn(blockVolumeService);
    }

    @Test
    void testAttachVolume() throws OpenstackException {
        // given
        when(blockVolumeService.attach(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ActionResponse.actionSuccess());

        // when
        openstackCinderService.attachVolume(server, volume, DEVICE, clientV3Mock);

        // then
        verifyAttachVolumeExecution();
        verify(volume).getName();
        verify(server).getName();
    }

    @Test
    void testAttachVolumeFailed() {
        // given
        when(blockVolumeService.attach(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));
        when(server.getName()).thenReturn(SERVER_NAME);
        when(volume.getName()).thenReturn(VOLUME_NAME);
        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.attachVolume(server, volume, DEVICE, clientV3Mock));

        // then
        verifyAttachVolumeExecution();
        verify(volume, times(2)).getName();
        verify(server, times(2)).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(ATTACH_VOLUME_FAILED);
    }

    private void verifyAttachVolumeExecution() {
        verify(blockVolumeService).attach(eq(VOLUME_ID), eq(SERVER_ID), eq(DEVICE), eq(SERVER_HOST_ID));
        verify(volume).getId();
        verify(server).getHostId();
        verify(server).getId();
    }
}

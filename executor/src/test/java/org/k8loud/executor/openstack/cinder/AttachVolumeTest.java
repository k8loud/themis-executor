package org.k8loud.executor.openstack.cinder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.model.common.ActionResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.ATTACH_VOLUME_FAILED;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachVolumeTest extends OpenstackCinderBaseTest {
    public static final String SERVER_HOST_ID = "hostId";

    @Override
    protected void setUp() {
        when(serverMock.getId()).thenReturn(SERVER_ID);
        when(serverMock.getHostId()).thenReturn(SERVER_HOST_ID);
        when(volumeMock.getId()).thenReturn(VOLUME_ID);

        when(clientV3Mock.blockStorage()).thenReturn(blockStorageServiceMock);
        when(blockStorageServiceMock.volumes()).thenReturn(blockVolumeServiceMock);
    }

    @Test
    void testAttachVolume() throws OpenstackException {
        // given
        when(blockVolumeServiceMock.attach(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ActionResponse.actionSuccess());

        // when
        openstackCinderService.attachVolume(serverMock, volumeMock, DEVICE, clientV3Mock);

        // then
        verifyAttachVolumeExecution();
        verify(volumeMock).getName();
        verify(serverMock).getName();
    }

    @Test
    void testAttachVolumeFailed() {
        // given
        when(blockVolumeServiceMock.attach(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ActionResponse.actionFailed(EXCEPTION_MESSAGE, 123));
        when(serverMock.getName()).thenReturn(SERVER_NAME);
        when(volumeMock.getName()).thenReturn(VOLUME_NAME);
        // when
        Throwable throwable = catchThrowable(
                () -> openstackCinderService.attachVolume(serverMock, volumeMock, DEVICE, clientV3Mock));

        // then
        verifyAttachVolumeExecution();
        verify(volumeMock, times(2)).getName();
        verify(serverMock, times(2)).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage(EXCEPTION_MESSAGE);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(ATTACH_VOLUME_FAILED);
    }

    private void verifyAttachVolumeExecution() {
        verify(blockVolumeServiceMock).attach(eq(VOLUME_ID), eq(SERVER_ID), eq(DEVICE), eq(SERVER_HOST_ID));
        verify(volumeMock).getId();
        verify(serverMock).getHostId();
        verify(serverMock).getId();
    }
}

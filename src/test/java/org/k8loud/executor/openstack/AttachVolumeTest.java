package org.k8loud.executor.openstack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.storage.block.Volume;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.VOLUME_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AttachVolumeTest extends OpenstackBaseTest {
    @Mock
    Volume volumeMock;

    @Override
    protected void setUp() throws OpenstackException {
        when(openstackNovaServiceMock.getServer(anyString(), any(OSClient.OSClientV3.class))).thenReturn(serverMock);
        when(openstackCinderService.getVolume(anyString(), any(OSClient.OSClientV3.class))).thenReturn(volumeMock);
    }

    @Test
    void testAttachVolumeSuccess() throws OpenstackException {
        // given
        when(volumeMock.getStatus()).thenReturn(Volume.Status.AVAILABLE);

        // when
        Map<String, String> res = openstackService.attachVolume(REGION, SERVER_ID, VOLUME_ID, DEVICE);

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackCinderService).getVolume(eq(VOLUME_ID), eq(clientV3Mock));
        verify(volumeMock).getStatus();
        verify(openstackCinderService).attachVolume(eq(serverMock), eq(volumeMock), eq(DEVICE), eq(clientV3Mock));
        assertResult(String.format("Attaching volume with id=%s for a server with id=%s to device=%s finished with " +
                        "success", VOLUME_ID, SERVER_ID, DEVICE), res);
    }

    @ParameterizedTest
    @EnumSource(value = Volume.Status.class, names = "AVAILABLE", mode = EnumSource.Mode.EXCLUDE)
    void testAttachVolumeFailed(Volume.Status status) throws OpenstackException {
        // given
        when(volumeMock.getStatus()).thenReturn(status);
        when(volumeMock.getName()).thenReturn(VOLUME_NAME);

        // when
        Throwable throwable = catchThrowable(() -> openstackService.attachVolume(REGION, SERVER_ID, VOLUME_ID, DEVICE));

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackCinderService).getVolume(eq(VOLUME_ID), eq(clientV3Mock));
        verify(volumeMock, times(2)).getStatus();
        verify(volumeMock).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Volume %s has status %s, but available is needed", VOLUME_NAME, status);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(VOLUME_ERROR);
    }
}

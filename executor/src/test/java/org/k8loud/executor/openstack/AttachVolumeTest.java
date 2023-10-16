package org.k8loud.executor.openstack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.VOLUME_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.openstack4j.model.compute.Server.Status.VERIFY_RESIZE;

@ExtendWith(MockitoExtension.class)
public class AttachVolumeTest {
    private static final String REGION = "region";
    private static final String SERVER_ID = "serverId";
    private static final String VOLUME_ID = "volumeId";
    private static final String DEVICE = "/dev/test";
    private static final String VOLUME_NAME = "volumeName";

    @Mock
    OpenstackClientProvider openstackClientProviderMock;
    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    Server server;
    @Mock
    Volume volume;
    @Mock
    OpenstackNovaService openstackNovaServiceMock;
    @Mock
    OpenstackCinderService openstackCinderService;

    OpenstackService openstackService;


    @BeforeEach
    void setUp() throws OpenstackException {
        openstackService = new OpenstackServiceImpl(openstackClientProviderMock, openstackNovaServiceMock,
                openstackCinderService);

        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);
        when(openstackNovaServiceMock.getServer(anyString(), any(OSClient.OSClientV3.class))).thenReturn(server);
        when(openstackCinderService.getVolume(anyString(), any(OSClient.OSClientV3.class))).thenReturn(volume);

    }

    @Test
    void testResizeServerSuccess() throws OpenstackException {
        // given
        when(volume.getStatus()).thenReturn(Volume.Status.AVAILABLE);

        // when
        openstackService.attachVolume(REGION, SERVER_ID, VOLUME_ID, DEVICE);

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackCinderService).getVolume(eq(VOLUME_ID), eq(clientV3Mock));
        verify(volume).getStatus();
        verify(openstackCinderService).attachVolume(eq(server), eq(volume), eq(DEVICE), eq(clientV3Mock));
    }

    @ParameterizedTest
    @EnumSource(value = Volume.Status.class, names = "AVAILABLE", mode = EnumSource.Mode.EXCLUDE)
    void testDetachVolumeFailed(Volume.Status status) throws OpenstackException {
        // given
        when(volume.getStatus()).thenReturn(status);
        when(volume.getName()).thenReturn(VOLUME_NAME);

        // when
        Throwable throwable = catchThrowable(() -> openstackService.attachVolume(REGION, SERVER_ID, VOLUME_ID, DEVICE));

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackCinderService).getVolume(eq(VOLUME_ID), eq(clientV3Mock));
        verify(volume, times(3)).getStatus();
        verify(volume, times(2)).getName();

        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Volume %s has status %s, but available is needed", VOLUME_NAME, status);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(VOLUME_ERROR);
    }
}

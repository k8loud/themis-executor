package org.k8loud.executor.openstack;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CopyServerTest extends OpenstackBaseTest {
    private static final String FLAVOR_ID = "flavorId";
    private static final String IMAGE_ID = "imageId";

    @Test
    void testCopyServerSuccess() throws OpenstackException {
        // given
        setUpMocks();

        // when
        openstackService.copyServer(REGION, SERVER_ID);

        // then
        verify(clientV3Mock).useRegion(eq(REGION));
        verify(openstackNovaServiceMock).getServer(eq(SERVER_ID), eq(clientV3Mock));
        verify(openstackNovaServiceMock).createServer(eq(SERVER_NAME + "-copy"), eq(FLAVOR_ID), eq(IMAGE_ID), anyInt(),
                eq(clientV3Mock));
    }

    //FIXME: Tests should use mocked and autowired objects -> cannot run aspects code right now
    //    @Test
    //    void testCopyServerFailed() throws OpenstackException {
    //        // given
    //        when(openstackClientProviderMock.getClientFromToken()).thenThrow(new OpenstackException(new
    //        AuthenticationException("message", 123), AUTHENTICATION_ERROR));
    //
    //        // when
    //        Throwable throwable = catchThrowable(() -> openstackService.copyServer(REGION, SERVER_ID));
    //
    //        // then
    //        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class);
    //        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(COPY_SERVER_FAILED);
    //    }

    private void setUpMocks() throws OpenstackException {
        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);

        when(openstackNovaServiceMock.getServer(anyString(), any(OSClient.OSClientV3.class))).thenReturn(serverMock);
        when(serverMock.getName()).thenReturn(SERVER_NAME);
        when(serverMock.getFlavorId()).thenReturn(FLAVOR_ID);
        when(serverMock.getImageId()).thenReturn(IMAGE_ID);

        doNothing().when(openstackNovaServiceMock)
                .createServer(anyString(), anyString(), anyString(), anyInt(), any(OSClient.OSClientV3.class));
    }
}

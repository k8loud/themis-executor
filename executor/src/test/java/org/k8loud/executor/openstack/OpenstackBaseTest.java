package org.k8loud.executor.openstack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class OpenstackBaseTest extends OpenstackConstants {
    @Mock
    Server serverMock;
    @Mock
    OpenstackClientProvider openstackClientProviderMock;
    @Mock
    OSClient.OSClientV3 clientV3Mock;
    @Mock
    OpenstackNovaService openstackNovaServiceMock;
    @Mock
    OpenstackCinderService openstackCinderService;
    @Mock
    OpenstackGlanceService openstackGlanceService;

    OpenstackService openstackService;

    @BeforeEach
    public void baseSetUp() throws OpenstackException {
        openstackService = new OpenstackServiceImpl(openstackClientProviderMock, openstackNovaServiceMock, openstackCinderService, openstackGlanceService);
        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);
    }
}

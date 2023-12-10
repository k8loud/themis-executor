package org.k8loud.executor.openstack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class OpenstackBaseTest extends OpenstackConstants {
    @Mock
    protected Server serverMock;
    @Mock
    protected OpenstackClientProvider openstackClientProviderMock;
    @Mock
    protected OSClient.OSClientV3 clientV3Mock;
    @Mock
    protected OpenstackNovaService openstackNovaServiceMock;
    @Mock
    protected OpenstackCinderService openstackCinderService;
    @Mock
    protected OpenstackGlanceService openstackGlanceService;
    @Mock
    protected OpenstackNeutronService openstackNeutronService;

    protected OpenstackService openstackService;

    @BeforeEach
    protected void baseSetUp() throws OpenstackException {
        openstackService = new OpenstackServiceImpl(openstackClientProviderMock, openstackNovaServiceMock,
                openstackCinderService, openstackGlanceService, openstackNeutronService, null);
        when(openstackClientProviderMock.getClientFromToken()).thenReturn(clientV3Mock);
        setUp();
    }

    protected abstract void setUp() throws OpenstackException;

    protected void assertResult(String expectedResult, Map<String, Object> result){
        assertThat(result.get("result")).isEqualTo(expectedResult);
    }
}

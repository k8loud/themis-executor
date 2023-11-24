package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.service.DataStorageService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KubernetesBaseTest {
    protected static final String NAMESPACE = "namespaceValue";
    protected static final String RESOURCE_NAME = "resourceNameValue";
    @Mock
    protected KubernetesClientProvider kubernetesClientProviderMock;
    @Mock
    protected DataStorageService dataStorageServiceMock;
    protected KubernetesServer server;
    protected KubernetesClient client;
    protected KubernetesService kubernetesService;

    @BeforeEach
    public void setUp() {
        server = new KubernetesServer(true, true);
        server.before();
        client = server.getClient();

        when(kubernetesClientProviderMock.getClient()).thenReturn(client);
        kubernetesService = new KubernetesServiceImpl(kubernetesClientProviderMock, dataStorageServiceMock);

        additionalSetUp();
    }

    public void additionalSetUp() {
        // empty
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.after();
        }
    }

    protected void assertResult(String expectedResult, Map<String, String> result){
        assertThat(result.get("result")).isEqualTo(expectedResult);
    }
}

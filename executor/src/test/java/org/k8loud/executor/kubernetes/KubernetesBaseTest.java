package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.common.testutil.DataStorageTestUtil;
import org.k8loud.executor.service.DataStorageService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

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

        lenient().when(kubernetesClientProviderMock.getClient()).thenReturn(client);
        lenient().doAnswer(i -> DataStorageTestUtil.store(i.getArgument(0), i.getArgument(1)))
                .when(dataStorageServiceMock).store(anyString(), anyString());
        lenient().doAnswer(i -> DataStorageTestUtil.remove(i.getArgument(0)))
                .when(dataStorageServiceMock).remove(anyString());
        kubernetesService = new KubernetesServiceImpl(kubernetesClientProviderMock, dataStorageServiceMock);
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.after();
        }
    }
}

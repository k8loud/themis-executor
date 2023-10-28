package org.k8loud.executor.action.kubernetes;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.kubernetes.KubernetesClientProvider;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.kubernetes.KubernetesServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseTest {
    @Mock
    protected KubernetesClientProvider kubernetesClientProvider;
    protected KubernetesServer server;
    protected KubernetesClient client;
    protected KubernetesService kubernetesService;

    @BeforeEach
    public void setUp() {
        server = new KubernetesServer(true, true);
        server.before();
        client = server.getClient();

        when(kubernetesClientProvider.getClient()).thenReturn(client);
        kubernetesService = new KubernetesServiceImpl(kubernetesClientProvider);
    }

//    @AfterEach
//    public void tearDown() {
//        if (server != null) {
//            server.after();
//        }
//    }
}

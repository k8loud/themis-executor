package org.k8loud.executor.actions.kubernetes;

import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.actions.ActionBaseTest;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public abstract class KubernetesActionBaseTest extends ActionBaseTest {
    protected static final String NAMESPACE_KEY = "namespace";
    protected static final String RESOURCE_NAME_KEY = "resourceName";
    protected static final String RESOURCE_TYPE_KEY = "resourceType";
    protected static final String NAMESPACE = "namespaceValue";
    protected static final String RESOURCE_NAME = "resourceNameValue";
    protected static final String RESOURCE_TYPE = "resourceTypeValue";

    @Mock
    protected KubernetesService kubernetesServiceMock;
}

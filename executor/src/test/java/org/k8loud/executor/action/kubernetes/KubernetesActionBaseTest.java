package org.k8loud.executor.action.kubernetes;

import org.k8loud.executor.action.ActionBaseTest;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.mockito.Mock;

public abstract class KubernetesActionBaseTest extends ActionBaseTest {
    protected static final String NAMESPACE_KEY = "namespace";
    protected static final String RESOURCE_NAME_KEY = "resourceName";
    protected static final String RESOURCE_TYPE_KEY = "resourceType";
    protected static final String NAMESPACE = "namespaceValue";
    protected static final String RESOURCE_NAME = "resourceNameValue";
    protected static final String RESOURCE_TYPE = "resourceTypeValue";
    protected static final String RESULT = "result";

    @Mock
    protected KubernetesService kubernetesServiceMock;
}

package org.k8loud.executor.action.openstack;

import org.k8loud.executor.action.ActionBaseTest;
import org.k8loud.executor.openstack.OpenstackService;
import org.mockito.Mock;

import static org.mockito.Mockito.verifyNoInteractions;

public abstract class OpenstackActionBaseTest extends ActionBaseTest {
    protected static final String REGION = "regionTest";
    protected static final String SERVER_ID = "123-server-id-123";
    protected static final String VOLUME_ID = "123-volume-id-123";
    protected static final String FLAVOR_ID = "123-flavor-id-123";

    @Mock
    protected OpenstackService openstackServiceMock;

    @Override
    protected void assertMissingParamException(Throwable throwable, String missingParam) {
        super.assertMissingParamException(throwable, missingParam);
        verifyNoInteractions(openstackServiceMock);
    }
}

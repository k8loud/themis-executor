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
    protected static final String IMAGE_ID = "123-image-id-123";
    protected static final String SECURITY_GROUP_ID = "123-security-group-id-123";
    protected static final String SECURITY_GROUP_RULE_ID = "123-security-group-rule-id-123";
    public static final String ETHERTYPE = "123-ethertype-123";
    public static final String DIRECTION = "123-direction-123";
    public static final String REMOTE_IP_PREFIX = "123-remoteIpPrefix-123";
    public static final String PROTOCOL = "123-protocol-123";
    public static final String PORT_RANGE_MIN = "1";
    public static final String PORT_RANGE_MAX = "123";

    @Mock
    protected OpenstackService openstackServiceMock;

    @Override
    protected void assertMissingParamException(Throwable throwable, String missingParam) {
        super.assertMissingParamException(throwable, missingParam);
        verifyNoInteractions(openstackServiceMock);
    }
}

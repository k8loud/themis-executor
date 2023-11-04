package org.k8loud.executor.openstack.neutron;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.OpenstackException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.Builders;
import org.openstack4j.api.networking.SecurityGroupService;
import org.openstack4j.model.network.SecurityGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.ATTACH_VOLUME_FAILED;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.CREATE_SECURITY_GROUP_FAILED;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateSecurityGroupTest extends OpenstackNeutronBaseTest {
    private static final SecurityGroup SECURITY_GROUP = Builders.securityGroup()
            .name(SECURITY_GROUP_NAME)
            .description(SECURITY_GROUP_DESCRIPTION)
            .build();
    @Mock
    SecurityGroupService securityGroupServiceMock;
    @Mock
    SecurityGroup securityGroupMock;

    @Override
    protected void setUp() {
        when(networkingServiceMock.securitygroup()).thenReturn(securityGroupServiceMock);
    }

    @Test
    void testCreateSecurityGroup() throws OpenstackException {
        // given
        when(securityGroupServiceMock.create(any(SecurityGroup.class))).thenReturn(securityGroupMock);

        // when
        openstackNeutronService.createSecurityGroup(SECURITY_GROUP_NAME, SECURITY_GROUP_DESCRIPTION, clientV3Mock);

        // then
        verify(securityGroupServiceMock).create(refEq(SECURITY_GROUP));
    }

    @Test
    void testCreateSecurityGroupFailed() {
        // given
        when(securityGroupServiceMock.create(any(SecurityGroup.class))).thenReturn(null);

        // when
        Throwable throwable = catchThrowable(
                () -> openstackNeutronService.createSecurityGroup(SECURITY_GROUP_NAME, SECURITY_GROUP_DESCRIPTION,
                        clientV3Mock));

        // then
        verify(securityGroupServiceMock).create(refEq(SECURITY_GROUP));


        assertThat(throwable).isExactlyInstanceOf(OpenstackException.class)
                .hasMessage("Failed to create SecurityGroup with name \"%s\"", SECURITY_GROUP_NAME);
        assertThat(((OpenstackException) throwable).getExceptionCode()).isSameAs(CREATE_SECURITY_GROUP_FAILED);
    }
}

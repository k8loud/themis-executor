package org.k8loud.executor.action.openstack.neutron;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.CreateSecurityGroupAction;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CreateSecurityGroupActionTest extends OpenstackActionBaseTest {
    private static final String SECURITY_GROUP_NAME = "test-name";
    private static final String SECURITY_GROUP_DESCRIPTION = "test description";

    private static final Params VALID_PARAMS = new Params(
            Map.of("region", REGION, "name", SECURITY_GROUP_NAME, "description", SECURITY_GROUP_DESCRIPTION));

    @Test
    void testCreateSecurityGroupAction() throws ActionException, OpenstackException {
        // given
        CreateSecurityGroupAction createSecurityGroupAction = new CreateSecurityGroupAction(VALID_PARAMS,
                openstackServiceMock);
        when(openstackServiceMock.createSecurityGroup(anyString(), anyString(), anyString())).thenReturn(RESULT);

        // when
        ExecutionRS response = createSecurityGroupAction.execute();

        // then
        verify(openstackServiceMock).createSecurityGroup(eq(REGION), eq(SECURITY_GROUP_NAME),
                eq(SECURITY_GROUP_DESCRIPTION));
        assertSuccessResponse(response);
    }

    @ParameterizedTest
    @MethodSource
    void testCreateSecurityGroupActionWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new CreateSecurityGroupAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testCreateSecurityGroupActionWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("name", SECURITY_GROUP_NAME)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "description", SECURITY_GROUP_DESCRIPTION)), "name"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "name", SECURITY_GROUP_NAME)), "description"));
    }
}

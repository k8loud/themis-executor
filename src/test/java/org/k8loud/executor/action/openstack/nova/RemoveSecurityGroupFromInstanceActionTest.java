package org.k8loud.executor.action.openstack.nova;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.AddSecurityGroupToInstanceAction;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.action.openstack.RemoveSecurityGroupFromInstanceAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveSecurityGroupFromInstanceActionTest extends OpenstackActionBaseTest {
    @Test
    void testSuccess() throws ActionException, OpenstackException {
        // given
        Params validParams = new Params(
                Map.of("region", REGION, "securityGroupId", SECURITY_GROUP_ID, "serverId", SERVER_ID));
        RemoveSecurityGroupFromInstanceAction groupFromInstanceAction =
                new RemoveSecurityGroupFromInstanceAction(validParams, openstackServiceMock);

        when(openstackServiceMock.removeSecurityGroupFromInstance(anyString(), anyString(), anyString()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = groupFromInstanceAction.execute();

        // then
        verify(openstackServiceMock).removeSecurityGroupFromInstance(eq(REGION), eq(SECURITY_GROUP_ID), eq(SERVER_ID));
        assertSuccessResponse(response);
    }


    @ParameterizedTest
    @MethodSource
    void testWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new AddSecurityGroupToInstanceAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("securityGroupId", SECURITY_GROUP_ID)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION)), "securityGroupId"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "securityGroupId", SECURITY_GROUP_ID)), "serverId")
        );
    }
}

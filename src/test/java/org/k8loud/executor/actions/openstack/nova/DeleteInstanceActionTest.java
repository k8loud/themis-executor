package org.k8loud.executor.actions.openstack.nova;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.openstack.DeleteInstanceAction;
import org.k8loud.executor.actions.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteInstanceActionTest extends OpenstackActionBaseTest {
    private static final String NAME = "name";

    @Test
    void testNamePatter() throws ActionException, OpenstackException, ValidationException {
        // given
        Params params = new Params(Map.of("name", NAME, "region", REGION));
        DeleteInstanceAction deleteInstanceAction = new DeleteInstanceAction(
                params, openstackServiceMock);
        when(openstackServiceMock.deleteServers(anyString(), anyString()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = deleteInstanceAction.execute();

        // then
        verify(openstackServiceMock).deleteServers(eq(REGION), eq(NAME));
        assertSuccessResponse(response);
    }

    @Test
    void testServerList() throws ActionException, OpenstackException, ValidationException {
        // given
        Params params = new Params(Map.of("name", NAME, "region", REGION, "serverIds", SERVER_IDS));
        DeleteInstanceAction deleteInstanceAction = new DeleteInstanceAction(
                params, openstackServiceMock);
        when(openstackServiceMock.deleteServers(anyString(), anyList()))
                .thenReturn(resultMap);

        // when
        ExecutionRS response = deleteInstanceAction.execute();

        // then
        verify(openstackServiceMock).deleteServers(eq(REGION), eq(List.of(SERVER_IDS.split(","))));
        assertSuccessResponse(response);
    }


    @ParameterizedTest
    @MethodSource
    void testWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new DeleteInstanceAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("name", NAME)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION)), "name")
        );
    }
}

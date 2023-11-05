package org.k8loud.executor.action.openstack.nova;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.openstack.AddInstanceAction;
import org.k8loud.executor.action.openstack.OpenstackActionBaseTest;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AddInstanceActionTest extends OpenstackActionBaseTest {
    private static final String NAME = "name";
    private static final String SECURITY_GROUP = "sec-group";
    private static final String USER_DATA = "user-data";

    private static final String COUNT = "3";


    @ParameterizedTest
    @MethodSource
    void testSuccess(Params params) throws ActionException, OpenstackException {
        // given
        AddInstanceAction addInstanceAction = new AddInstanceAction(
                params, openstackServiceMock);
        when(openstackServiceMock.createServers(anyString(), anyString(), anyString(), anyString(),
                nullable(String.class), nullable(String.class), nullable(String.class), anyInt()))
                .thenReturn(RESULT);

        // when
        ExecutionRS response = addInstanceAction.execute();

        // then
        String keypair = params.getParams().getOrDefault("keypairName", "default");
        String securityGroup = params.getParams().get("securityGroup");
        String userData = params.getParams().get("userData");
        int count = Integer.parseInt(params.getParams().getOrDefault("count", "1"));
        verify(openstackServiceMock).createServers(eq(REGION), eq(NAME), eq(IMAGE_ID), eq(FLAVOR_ID), eq(keypair),
                eq(securityGroup), eq(userData), eq(count));
        assertSuccessResponse(response);
    }

    private static Stream<Params> testSuccess() {
        return Stream.of(
                new Params(Map.of("region", REGION, "name", NAME, "imageId", IMAGE_ID, "flavorId", FLAVOR_ID)),
                new Params(Map.of("region", REGION, "name", NAME, "imageId", IMAGE_ID, "flavorId", FLAVOR_ID,
                        "securityGroup", SECURITY_GROUP)),
                new Params(
                        Map.of("region", REGION, "name", NAME, "imageId", IMAGE_ID, "flavorId", FLAVOR_ID,
                                "userData", USER_DATA)),
                new Params(Map.of("region", REGION, "name", NAME, "imageId", IMAGE_ID, "flavorId", FLAVOR_ID,
                        "count", COUNT))

        );
    }

    @ParameterizedTest
    @MethodSource
    void testWrongParams(Params invalidParams, String missingParam) {
        // when
        Throwable throwable = catchThrowable(
                () -> new AddInstanceAction(invalidParams, openstackServiceMock));

        // then
        assertMissingParamException(throwable, missingParam);
    }

    private static Stream<Arguments> testWrongParams() {
        return Stream.of(
                Arguments.of(
                        new Params(Map.of("name", NAME)), "region"),
                Arguments.of(
                        new Params(Map.of("region", REGION)), "name"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "name", NAME)), "imageId"),
                Arguments.of(
                        new Params(Map.of("region", REGION, "name", NAME, "imageId", IMAGE_ID)), "flavorId")
        );
    }
}

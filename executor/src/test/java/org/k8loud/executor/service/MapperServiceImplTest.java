package org.k8loud.executor.service;

import data.ExecutionRQ;
import data.Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.exception.code.ActionExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static data.ExecutionRQ.createExecutionRQ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class MapperServiceImplTest {
    private static final String INVALID = "invalidName";
    private static final Params VALID_PARAMS = new Params(Map.of("param1", "val1"));
    public static final Params EMPTY_PARAMS = new Params(Collections.emptyMap());

    @Autowired
    private MapperServiceImpl mapperService;

    @ParameterizedTest
    @MethodSource("provideMappableExecutionRQs")
    void testValidMapping(String collectionName, String actionName,
                          Params params) throws MapperException, ActionException {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertEquals(executionRQ.getActionName(), action.getClass().getSimpleName());
        checkFieldsValues(action, executionRQ.getParams());
    }

    @ParameterizedTest
    @MethodSource("provideUnmappableExecutionRQs")
    void testInvalidMapping(String collectionName, String actionName, Params params,
                            Class<? extends CustomException> expectedExceptionClass, Enum expectedExceptionCode) {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Throwable e = catchThrowable(() -> mapperService.map(executionRQ));

        // then
        assertThat(e).isExactlyInstanceOf(expectedExceptionClass);
        assertThat(((CustomException) e).getExceptionCode()).isEqualTo(expectedExceptionCode);
    }

    private static Stream<Arguments> provideMappableExecutionRQs() {
        Params horizontalKubernetesScalingParams = new Params(
                Map.of("resourceName", "nameVal", "resourceType", "typeVal", "namespace", "namespaceVal", "replicas",
                        "420"));
        Params horizontalOpenstackScalingParams = new Params(Map.of("region", "regionVal", "serverId", "EUNE"));
        Params verticalOpenstackScalingParams = new Params(
                Map.of("region", "regionVal", "serverId", "EUNE", "flavorId", "asdad123312"));

        return Stream.of(Arguments.of("kubernetes", "DeletePodAction", EMPTY_PARAMS),
                Arguments.of("kubernetes", "HorizontalScalingAction", horizontalKubernetesScalingParams),
                Arguments.of("openstack", "HorizontalScalingAction", horizontalOpenstackScalingParams),
                Arguments.of("openstack", "VerticalScalingAction", verticalOpenstackScalingParams)
// TODO: How to handle map?
//                Arguments.of("kubernetes", "UpdateConfigMapAction", Map.of("namespace", "nameVal",
//                        "resourceName", "typeVal", "replacements", Map.of("k1", "v1")))
        );
    }

    private static Stream<Arguments> provideUnmappableExecutionRQs() {
        // TODO: The VALID_PARAMS here aren't actually valid for all cases; it's not covered because in the specific
        //  Action implementations we access values by specific keys anyway, other keys will be ignored
        return Stream.of(Arguments.of("kubernetes", INVALID, VALID_PARAMS, ActionException.class,
                        ActionExceptionCode.ACTION_CLASS_NOT_FOUND),
                Arguments.of(INVALID, "DeletePodAction", VALID_PARAMS, ActionException.class,
                        ActionExceptionCode.ACTION_CLASS_NOT_FOUND));
    }

    private void checkFieldValue(Object object, String fieldName, String expectedValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            // FIXME: Handling doubles
            String typeName = field.getType().getName();
            if (typeName.equals("double") || typeName.equals("float")) {
                Assertions.assertEquals(Double.valueOf(expectedValue), field.get(object));
            } else {
                String value = String.valueOf(field.get(object));
                Assertions.assertEquals(expectedValue, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail();
        }
    }

    private void checkFieldsValues(Object object, Params params) {
        for (Map.Entry<String, String> entry : params.getParams().entrySet()) {
            checkFieldValue(object, entry.getKey(), entry.getValue());
        }
    }
}


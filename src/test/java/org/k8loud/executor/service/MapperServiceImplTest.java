package org.k8loud.executor.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.exception.code.ActionExceptionCode;
import org.k8loud.executor.model.ExecutionRQ;
import org.k8loud.executor.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.fail;
import static org.k8loud.executor.model.ExecutionRQ.createExecutionRQ;

@SpringBootTest
@AutoConfigureObservability
class MapperServiceImplTest {
    private static final String INVALID = "invalidName";
    private static final Params VALID_PARAMS = new Params(Map.of("param1", "val1"));

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
        Params deleteResourceActionParams = new Params(Map.of("resourceName", "nameVal", "resourceType",
                "typeVal", "namespace", "namespaceVal"));
        Params horizontalKubernetesScalingParams = new Params(
                Map.of("resourceName", "nameVal", "resourceType", "typeVal", "namespace",
                        "namespaceVal", "replicas", "420"));
        Params verticalOpenstackScalingParams = new Params(
                Map.of("region", "regionVal", "serverId", "EUNE", "flavorId", "asdad123312"));
        Params customScriptParams = new Params(Map.of("host", "127.0.0.1", "port", "1337",
                "privateKey", "a#$t9hgfd1", "user", "root", "command", "echo hello"));

        return Stream.of(
                Arguments.of("kubernetes", "DeleteResourceAction", deleteResourceActionParams),
                Arguments.of("kubernetes", "HorizontalScalingAction", horizontalKubernetesScalingParams),
                Arguments.of("openstack", "VerticalScalingUpAction", verticalOpenstackScalingParams),
                Arguments.of("command", "CustomScriptAction", customScriptParams)
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
                Arguments.of(INVALID, "DeleteResourceAction", VALID_PARAMS, ActionException.class,
                        ActionExceptionCode.ACTION_CLASS_NOT_FOUND));
    }

    private void checkFieldValue(Object object, @NotNull Class<?> clazz, String fieldName, String expectedValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            String value = String.valueOf(field.get(object));
            Assertions.assertEquals(expectedValue, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                checkFieldValue(object, superClazz, fieldName, expectedValue);
            } else {
                fail();
            }
        }
    }

    private void checkFieldsValues(Object object, Params params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            checkFieldValue(object, object.getClass(), entry.getKey(), entry.getValue());
        }
    }
}

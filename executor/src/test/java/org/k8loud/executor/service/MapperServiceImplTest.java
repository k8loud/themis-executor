package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.exception.code.MapperExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static data.ExecutionRQ.createExecutionRQ;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class MapperServiceImplTest {
    private static final String INVALID = "invalidName";
    private static final Map<String, String> VALID_PARAMS = Map.of("param1", "val1");

    @Autowired
    private MapperServiceImpl mapperService;

    @ParameterizedTest
    @MethodSource("provideMappableExecutionRQs")
    void testValidMapping(String collectionName, String actionName, Map<String, String> params) throws MapperException {
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
    void testInvalidMapping(String collectionName, String actionName, Map<String, String> params,
                            MapperExceptionCode expectedExceptionCode) {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Throwable e = catchThrowable(() -> mapperService.map(executionRQ));

        // then
        Assertions.assertEquals(MapperException.class, e.getClass());
        Assertions.assertEquals(expectedExceptionCode, ((CustomException) e).getExceptionCode());
    }

    private static Stream<Arguments> provideMappableExecutionRQs() {
        return Stream.of(
                Arguments.of("kubernetes", "DeletePodAction", Collections.emptyMap()),
                Arguments.of("kubernetes", "HorizontalScalingAction", Map.of("resourceName", "nameVal",
                        "resourceType", "typeVal", "namespace", "namespaceVal", "replicas", "420")),
                Arguments.of("openstack", "HorizontalScalingAction", Map.of("region", "regionVal",
                        "serverId", "EUNE")),
                Arguments.of("openstack", "VerticalScalingAction", Map.of("region", "regionVal",
                        "serverId", "EUNE", "diskResizeValue", "2.12323123", "ramResizeValue", "-0.11",
                        "vcpusResizeValue", "5768763425"))
// TODO: How to handle map?
//                Arguments.of("kubernetes", "UpdateConfigMapAction", Map.of("namespace", "nameVal",
//                        "resourceName", "typeVal", "replacements", Map.of("k1", "v1")))
        );
    }

    private static Stream<Arguments> provideUnmappableExecutionRQs() {
        // TODO: The VALID_PARAMS here aren't actually valid for all cases; it's not covered because in the specific
        //  Action implementations we access values by specific keys anyway, other keys will be ignored
        return Stream.of(
                Arguments.of("kubernetes", INVALID, VALID_PARAMS, MapperExceptionCode.ACTION_CLASS_NOT_FOUND),
                Arguments.of(INVALID, "DeletePodAction", VALID_PARAMS, MapperExceptionCode.ACTION_CLASS_NOT_FOUND)
        );
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

    private void checkFieldsValues(Object object, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            checkFieldValue(object, entry.getKey(), entry.getValue());
        }
    }
}

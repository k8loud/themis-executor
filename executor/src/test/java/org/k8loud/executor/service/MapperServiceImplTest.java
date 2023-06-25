package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
class MapperServiceImplTest {
    private static final String INVALID = "invalidName";
    private static final Map<String, String> VALID_PARAMS = Map.of("param1", "val1");

    @Autowired
    private MapperServiceImpl mapperService;

    @ParameterizedTest
    @MethodSource("provideValidExecutionRQs")
    void testValidMapping(String collectionName, String actionName, Map<String, String> params) {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNotNull(action);
        Assertions.assertEquals(executionRQ.getActionName(), action.getClass().getSimpleName());
        // FIXME
//        Assertions.assertEquals(executionRQ.getParams(), action.getParams());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidExecutionRQs")
    void testInvalidMapping(String collectionName, String actionName, Map<String, String> params) {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNull(action);
    }

    private static ExecutionRQ createExecutionRQ(String collectionName, String actionName, Map<String, String> params) {
        return ExecutionRQ.builder()
                .collectionName(collectionName)
                .actionName(actionName)
                .params(params)
                .build();
    }

    private static Stream<Arguments> provideValidExecutionRQs() {
        return Stream.of(
                Arguments.of("kubernetes", "DeletePodAction", VALID_PARAMS),
                Arguments.of("kubernetes", "DeletePodAction", Collections.emptyMap()),
                Arguments.of("kubernetes", "DeletePodAction", null) //FIXME for now it is valid. Back to this after some Actions coded
        );
    }

    private static Stream<Arguments> provideInvalidExecutionRQs() {
        return Stream.of(
                Arguments.of("kubernetes", INVALID, VALID_PARAMS),
                Arguments.of(INVALID, "DeletePodAction", VALID_PARAMS),
                Arguments.of("kubernetes", null, VALID_PARAMS),
                Arguments.of(null, "DeletePodAction", VALID_PARAMS)
        );
    }
}

package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
class MapperServiceImplTest {
    private static final ExecutionRQ VALID_EXECUTION_RQ = ExecutionRQ.builder()
            .collectionName("kubernetes")
            .actionName("DeletePodAction")
            .params(Map.of(
                    "param1", "val1",
                    "param2", "val2",
                    "param3", "val3"
            ))
            .build();
    @Autowired
    private MapperServiceImpl mapperService;

    private static Stream<ExecutionRQ> provideValidExecutionRQs() {
        return Stream.of(
                VALID_EXECUTION_RQ,
                VALID_EXECUTION_RQ.withParams(new HashMap<>()) // emptyParams
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidExecutionRQs")
    void testValidMapping(ExecutionRQ executionRQ) {
        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNotNull(action);
        Assertions.assertEquals(executionRQ.getActionName(), action.getClass().getSimpleName());
        Assertions.assertEquals(executionRQ.getParams(), action.getParams());
    }

    private static Stream<ExecutionRQ> provideInvalidExecutionRQs() {
        return Stream.of(
                VALID_EXECUTION_RQ.withCollectionName("XYZ"), // badCollectionName
                VALID_EXECUTION_RQ.withActionName("XYZ") // badActionName
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidExecutionRQs")
    void testInvalidMapping(ExecutionRQ executionRQ) {
        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNull(action);
    }
}

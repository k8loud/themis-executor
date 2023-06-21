package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.action.Action;

import java.util.HashMap;
import java.util.Map;

public class MapperServiceImplTest {
    private MapperServiceImpl mapperService;
    private ExecutionRQ executionRQ;

    @BeforeEach
    public void beforeEach() {
        mapperService = new MapperServiceImpl();
        // valid executionRQ
        executionRQ = ExecutionRQ.builder()
                .collectionName("kubernetes")
                .actionName("DeletePodAction")
                .params(Map.of(
                        "param1", "val1",
                        "param2", "val2",
                        "param3", "val3"
                ))
                .build();
    }

    @Test
    void testValidMapping() {
        // given

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNotNull(action);
        Assertions.assertEquals(executionRQ.getActionName(), action.getClass().getSimpleName());
        Assertions.assertEquals(executionRQ.getParams(), action.getParams());
    }

    @Test
    void testInvalidMappingBadCollectionName() {
        // given
        executionRQ.setCollectionName("XYZ");

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNull(action);
    }

    @Test
    void testInvalidMappingBadActionName() {
        // given
        executionRQ.setActionName("XYZ");

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNull(action);
    }

    @Test
    void testValidMappingEmptyParams() {
        // given
        executionRQ.setParams(new HashMap<>());

        // when
        Action action = mapperService.map(executionRQ);

        // then
        Assertions.assertNotNull(action);
        Assertions.assertEquals(executionRQ.getActionName(), action.getClass().getSimpleName());
        Assertions.assertEquals(executionRQ.getParams(), action.getParams());
    }
}

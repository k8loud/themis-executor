package org.k8loud.executor.action;

import data.ExecutionExitCode;
import data.ExecutionRS;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionBaseTest {
    protected static final String RESULT = "result";

    protected void checkResponse(ExecutionRS response) {
        assertEquals(ExecutionExitCode.OK, response.getExitCode());
        assertEquals(RESULT, response.getResult());
    }
}

package org.k8loud.executor.action;

import data.ExecutionExitCode;
import data.ExecutionRS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public abstract class ActionBaseTest {
    protected static final String RESULT = "result";

    protected void assertSuccessResponse(ExecutionRS response) {
        assertEquals(ExecutionExitCode.OK, response.getExitCode());
        assertEquals(RESULT, response.getResult());
    }
}

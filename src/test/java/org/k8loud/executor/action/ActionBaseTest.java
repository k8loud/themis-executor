package org.k8loud.executor.action;

import org.k8loud.executor.model.ExecutionExitCode;
import org.k8loud.executor.model.ExecutionRS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.ActionException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.k8loud.executor.exception.code.ActionExceptionCode.UNPACKING_PARAMS_FAILURE;
import static org.k8loud.executor.util.Util.resultMap;

@ExtendWith(MockitoExtension.class)
public abstract class ActionBaseTest {
    protected static final String RESULT = "example result";

    protected final Map<String, String> resultMap = resultMap(RESULT);

    protected void assertSuccessResponse(ExecutionRS response) {
        assertEquals(ExecutionExitCode.OK, response.getExitCode());
        assertEquals(RESULT, response.getResult());
    }

    protected void assertMissingParamException(Throwable throwable, String missingParam) {
        assertThat(throwable).isExactlyInstanceOf(ActionException.class)
                .hasMessage("Param '%s' is declared as required and was not found", missingParam);
        assertThat(((ActionException) throwable).getExceptionCode()).isEqualTo(UNPACKING_PARAMS_FAILURE);
    }
}

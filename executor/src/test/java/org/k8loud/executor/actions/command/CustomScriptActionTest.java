package org.k8loud.executor.actions.command;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class CustomScriptActionTest extends CommandActionBaseTest {
    private static final String COMMAND = "echo hello";
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, HOST,
            PORT_KEY, PORT,
            PRIVATE_KEY_KEY, PRIVATE_KEY,
            USER_KEY, USER,
            "command", COMMAND
    ));

    @Test
    void testValidParams() throws CommandException, ActionException {
        // given
        CustomScriptAction customScriptAction = new CustomScriptAction(PARAMS, commandExecutionServiceMock);

        // when
        ExecutionRS response = customScriptAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(HOST), eq(Integer.parseInt(PORT)), eq(PRIVATE_KEY),
                eq(USER), eq(COMMAND));
        assertSuccessResponse(response);
    }
}

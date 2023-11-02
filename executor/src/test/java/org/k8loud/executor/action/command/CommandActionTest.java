package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class CommandActionTest extends CommandActionBaseTest {
    private static final String COMMAND = "echo hello";
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, HOST,
            PORT_KEY, PORT,
            PRIVATE_KEY_KEY, PRIVATE_KEY,
            USER_KEY, USER
    ));
    CommandAction commandAction;

    @BeforeEach
    public void setUp() throws ActionException {
        commandAction = new CommandAction(PARAMS, commandExecutionServiceMock) {
            @Override
            protected void unpackAdditionalParams(Params params) {

            }

            @Override
            protected String buildCommand() {
                return COMMAND;
            }
        };
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // when
        commandAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(HOST), eq(Integer.parseInt(PORT)), eq(PRIVATE_KEY),
                eq(USER), eq(COMMAND));
    }
}

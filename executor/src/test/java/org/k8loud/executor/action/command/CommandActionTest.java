package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

class CommandActionTest extends CommandActionBaseTest {
    private static final Params PARAMS = new Params(Map.of(
            "host", "192.168.13.37",
            "port", "22",
            "privateKey", "p4009jZSD+16k8xk",
            "user", "ubuntu"
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
                return null;
            }
        };
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // when
        commandAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(any(), anyInt(), any(), any(), any());
    }
}

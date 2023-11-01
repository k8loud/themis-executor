package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
        commandAction.perform();

        // then
        verify(commandExecutionServiceMock, times(1)).executeCommand(any(), any(), any(), any(), any());
    }
}

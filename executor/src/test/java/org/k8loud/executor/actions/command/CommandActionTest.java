package org.k8loud.executor.actions.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommandActionTest {
    private static final Params PARAMS = new Params(Map.of(
            "host", "192.168.13.37",
            "port", "22",
            "privateKey", "p4009jZSD+16k8xk",
            "user", "ubuntu"
    ));
    @Mock
    CommandExecutionService commandExecutionService;
    CommandAction commandAction;

    @BeforeEach
    public void setUp() throws ActionException {
        commandAction = new CommandAction(PARAMS, commandExecutionService) {
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
        verify(commandExecutionService, times(1)).executeCommand(any(), any(), any(), any(), any());
    }
}
